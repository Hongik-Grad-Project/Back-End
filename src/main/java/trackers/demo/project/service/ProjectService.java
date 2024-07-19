package trackers.demo.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.like.domain.LikeRedisConstants;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.dto.LikeElement;
import trackers.demo.like.dto.LikeElements;
import trackers.demo.like.dto.LikeInfo;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.project.domain.*;
import trackers.demo.project.domain.repository.*;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.dto.request.ProjectCreateSecondRequest;
import trackers.demo.project.dto.request.ReadProjectFilterCondition;
import trackers.demo.project.dto.request.ReadProjectSearchCondition;
import trackers.demo.project.dto.response.ProjectDetailResponse;
import trackers.demo.project.dto.response.ProjectResponse;

import org.springframework.data.domain.Pageable;

import java.util.*;

import static trackers.demo.global.exception.ExceptionCode.*;
import static trackers.demo.like.domain.LikeRedisConstants.*;
import static trackers.demo.project.domain.type.CompletedStatusType.*;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final CustomProjectRepository customProjectRepository;

    private final ProjectTargetRepository projectTargetRepository;

    private final TargetRepository targetRepository;

    private final ProjectSubjectRepository projectSubjectRepository;

    private final SubjectRepository subjectRepository;

    private final MemberRepository memberRepository;

    private final CustomLikeRepository customLikeRepository;

    private final RedisTemplate<String, Object> redisTemplate;


    public void saveProjectFirst(
            final Long memberId,
            final ProjectCreateFirstRequest request,
            final String imageUrl
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 프로젝트 생성 및 저장
        final Project newProject = Project.of(
                member,
                request.getIsRecruit(),
                request.getWantedMember(),
                request.getStartDate(),
                request.getEndDate(),
                request.getProjectTitle(),
                imageUrl);
        final Project project = projectRepository.save(newProject);

        // 프로젝트-대상 저장
        final Target target = targetRepository.findByTargetTitle(request.getTarget())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
        final ProjectTarget newProjectTarget = new ProjectTarget(null, project, target);
        projectTargetRepository.save(newProjectTarget);

        // 프로젝트-주제 저장
        final Subject subject = subjectRepository.findBySubjectTitle(request.getSubject())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBJECT));
        final ProjectSubject newProjectSubject = new ProjectSubject(null, project, subject);
        projectSubjectRepository.save(newProjectSubject);

    }

    public Long saveProjectSecond(
            final Long memberId,
            final ProjectCreateSecondRequest createRequest,
            final List<String> imageUrlList
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 임시 저장된 프로젝트
        final Project project = projectRepository.findByMemberAndCompletedStatus(member, NOT_COMPLETED);

        // 프로젝트 생성 (소제목, 본문, 사진)
        project.createProject(createRequest, imageUrlList);
        projectRepository.save(project);

        return project.getId();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(final Accessor accessor, final Long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        final ProjectTarget projectTarget = projectTargetRepository.findByProject(project);
        final Target target = targetRepository.getReferenceById(projectTarget.getTarget().getId());

        final ProjectSubject projectSubject = projectSubjectRepository.findByProject(project);
        final Subject subject = subjectRepository.getReferenceById(projectSubject.getSubject().getId());

        final LikeInfo likeInfo = getLikeInfoByProjectId(accessor.getMemberId(), projectId);

        return ProjectDetailResponse.projectDetail(
                project,
                target,
                subject,
                likeInfo.isLike(),
                likeInfo.getLikeCount());
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

    public void validateProjectByMember(final Long memberId, final CompletedStatusType statusType) {
        if(!projectRepository.existsByMemberIdAndCompletedStatus(memberId, statusType)){
            throw new AuthException(INVALID_NOT_COMPLETED_PROJECT_WITH_MEMBER);
        }
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
        log.info("LikeInfo Map 추출");
        final Map<Long, LikeInfo> likeInfoByProject = getLikeInfoByProjectIds(accessor.getMemberId(), projectIds);

        return projects.stream()
                .map(project -> ProjectResponse.of(
                        project,
                        likeInfoByProject.get(project.getId()).isLike(),
                        likeInfoByProject.get(project.getId()).getLikeCount()
                )).toList();
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

