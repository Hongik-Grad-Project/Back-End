package trackers.demo.gallery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.gallery.domain.repository.CustomProjectRepository;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.dto.LikeElement;
import trackers.demo.like.dto.LikeElements;
import trackers.demo.like.dto.LikeInfo;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.project.domain.*;
import trackers.demo.project.domain.repository.*;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;
import trackers.demo.gallery.dto.response.ProjectDetailResponse;
import trackers.demo.gallery.dto.response.ProjectResponse;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static trackers.demo.global.exception.ExceptionCode.*;
import static trackers.demo.like.domain.LikeRedisConstants.*;
import static trackers.demo.like.domain.LikeRedisConstants.generateLikeKey;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GalleryService {

    private final ProjectRepository projectRepository;

    private final CustomProjectRepository customProjectRepository;

    private final ProjectTargetRepository projectTargetRepository;

    private final ProjectTagRepository projectTagRepository;

    private final TagRepository tagRepository;

    private final TargetRepository targetRepository;

    private final MemberRepository memberRepository;

    private final CustomLikeRepository customLikeRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(final Accessor accessor, final Long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        // 프로젝트 대상
        final ProjectTarget projectTarget = projectTargetRepository.findByProject(project);
        final Target target = targetRepository.getReferenceById(projectTarget.getTarget().getId());
        final String targetName = target.getTargetTitle();

        // 프로젝트 태그
        final List<ProjectTag> projectTagList = projectTagRepository.findAllByProject(project);
        final ArrayList<String> tagList = projectTagList.stream()
                .map(projectTag -> tagRepository.findById(projectTag.getTag().getId())
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_TAG)))
                .map(Tag::getTagTitle)
                .collect(Collectors.toCollection(ArrayList::new));

        // 프로젝트 좋아요 정보
        final LikeInfo likeInfo = getLikeInfoByProjectId(accessor.getMemberId(), projectId);

        // 프로젝트 작성자
        final Member projectOwner = memberRepository.findById(project.getMember().getId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        return ProjectDetailResponse.projectDetail(
                project,
                tagList,
                targetName,
                likeInfo.isLike(),
                likeInfo.getLikeCount(),
                projectOwner);
    }

    private LikeInfo getLikeInfoByProjectId(final Long memberId, final Long projectId) {
        final String key = generateLikeKey(projectId);
        if(TRUE.equals(redisTemplate.hasKey(key))){
            return readLikeInfoFromCache(key, memberId);
        }

        final LikeElement likeElement = customLikeRepository.findLikesElementByProjectId(projectId)
                .orElse(LikeElement.empty(projectId));
        storeLikeInCache(likeElement);

        return new LikeInfo(likeElement.getLikeCount(), likeElement.isLike(memberId));
    }

    private LikeInfo readLikeInfoFromCache(final String key, final Long memberId) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        final boolean isLike = TRUE.equals(opsForSet.isMember(key, memberId));
        // EMPTY_MARKER를 제외하기 위해 1을 빼줌
        final long count = Objects.requireNonNull(opsForSet.size(key)) -1;
        return new LikeInfo(count, isLike);
    }

    private void storeLikeInCache(final LikeElement likeElement) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        final String key = generateLikeKey(likeElement.getProjectId());
        opsForSet.add(key, EMPTY_MARKER);
        final Set<Long> memberIds = likeElement.getMemberIds();
        if(!memberIds.isEmpty()){
            opsForSet.add(key, likeElement.getMemberIds().toArray());
        }
        redisTemplate.expire(key, LIKE_TTL);
    }

    public void validateProjectByProjectId(final Long projectId){
        if(!projectRepository.existsById(projectId)){
            throw new AuthException(NOT_FOUND_PROJECT);
        }
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjectsByCondition(
            final Accessor accessor,
            final Pageable pageable,
            final ReadProjectSearchCondition readProjectSearchCondition,
            final ReadProjectFilterCondition readProjectFilterCondition
    ) {
        log.info("조건에 맞는 프로젝트 검색");
        final Slice<Project> projects = customProjectRepository.findProjectsAllByCondition(
                readProjectSearchCondition,
                readProjectFilterCondition,
                pageable
        );

        log.info("프로젝트를 Dto로 가공하여 반환");
        return getProjectResponses(accessor, projects);
    }

    private List<ProjectResponse> getProjectResponses(
            final Accessor accessor,
            final Slice<Project> projects) {
        log.info("프로젝트 Id 리스트 추출");
        final List<Long> projectIds = projects.stream().map(Project::getId).toList();
        log.info("프로젝트 대상 리스트 추출");
        final Map<Long, String> targetNameByProject = getTargetNameByProject(projectIds);
        log.info("LikeInfo Map 추출");
        final Map<Long, LikeInfo> likeInfoByProject = getLikeInfoByProjectIds(accessor.getMemberId(), projectIds);

        return projects.stream()
                .map(project ->
                        ProjectResponse.of(
                            project,
                            targetNameByProject.get(project.getId()),
                            likeInfoByProject.get(project.getId()).isLike(),
                            likeInfoByProject.get(project.getId()).getLikeCount()
                )).toList();
    }

    private Map<Long, String> getTargetNameByProject(final List<Long> projectIds) {
        final Map<Long, String> targetNameByProject = new HashMap<>();

        for(final Long projectId : projectIds){
            final ProjectTarget projectTarget = projectTargetRepository.findByProjectId(projectId);
            final Target target = targetRepository.findById(projectTarget.getTarget().getId())
                    .orElseThrow(() -> new  BadRequestException(NOT_FOUND_TARGET));
            targetNameByProject.put(projectId, target.getTargetTitle());
        }

        return targetNameByProject;
    }

    private Map<Long, LikeInfo> getLikeInfoByProjectIds(final Long memberId, final List<Long> projectIds) {
        final Map<Long, LikeInfo> likeInfoByProject = new HashMap<>();

        final List<Long> nonCachedProjectIds = new ArrayList<>();
        for(final Long projectId : projectIds){
            final String key = generateLikeKey(projectId);
            if(TRUE.equals(redisTemplate.hasKey(key))){
                likeInfoByProject.put(projectId, readLikeInfoFromCache(key, memberId));
            } else {
                nonCachedProjectIds.add(projectId);
            }
        }

        log.info("캐시되지 않은 프로젝트들 캐시에 추가");
        if(!nonCachedProjectIds.isEmpty()){
            final List<LikeElement> likeElements = customLikeRepository.findLikeElementByProjectIds(nonCachedProjectIds);
            // 조회된 likeElements 리스트에 캐시에 없는 프로젝트 ID에 대해 빈 LikeElement를 추가
            likeElements.addAll(getEmptyLikeElements(likeElements, nonCachedProjectIds));
            // 각 LikeElement를 캐시에 저장
            likeElements.forEach(this::storeLikeInCache);
            // Map에 좋아요 정보 추가
            likeInfoByProject.putAll(new LikeElements(likeElements).toLikeInfo(memberId));
        }
        return likeInfoByProject;
    }

    private List<LikeElement> getEmptyLikeElements(
            final List<LikeElement> likeElements,
            final List<Long> nonCachedProjectIds) {
        return nonCachedProjectIds.stream()
                .filter(projectId -> doesNotContainProjectId(likeElements, projectId))
                .map(LikeElement::empty)
                .toList();
    }

    private boolean doesNotContainProjectId(final List<LikeElement> likeElements, final Long projectId) {
        return likeElements.stream()
                .noneMatch(likeElement -> likeElement.getProjectId().equals(projectId));
    }
}
