package trackers.demo.gallery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.gallery.domain.recommendstategy.RecommendStrategies;
import trackers.demo.gallery.domain.recommendstategy.RecommendStrategy;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.dto.LikeElement;
import trackers.demo.like.dto.LikeElements;
import trackers.demo.like.dto.LikeInfo;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTarget;
import trackers.demo.project.domain.Target;
import trackers.demo.project.domain.repository.ProjectRepository;
import trackers.demo.project.domain.repository.ProjectTargetRepository;
import trackers.demo.project.domain.repository.TargetRepository;

import java.util.*;

import static java.lang.Boolean.TRUE;
import static trackers.demo.gallery.domain.recommendstategy.RecommendType.*;
import static trackers.demo.global.exception.ExceptionCode.NOT_FOUND_TARGET;
import static trackers.demo.like.domain.LikeRedisConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecommendService {

    private static final int RECOMMEND_AMOUNT = 3;

    private final RecommendStrategies recommendStrategies;

    private final ProjectTargetRepository projectTargetRepository;

    private final CustomLikeRepository customLikeRepository;

    private final TargetRepository targetRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public List<ProjectResponse> getRecommendProjects(final Accessor accessor) {
        final RecommendStrategy recommendStrategy = recommendStrategies.mapByRecommendType(LIKE);
        final Pageable pageable = Pageable.ofSize(RECOMMEND_AMOUNT);
        final List<Project> projects = recommendStrategy.recommend(pageable);

        log.info("프로젝트 Id 리스트 추출");
        final List<Long> projectIds = projects.stream().map(Project::getId).toList();
        log.info("프로젝트 대상 리스트 추출");
        final Map<Long, String> targetNameByProject = getTargetNameByProject(projectIds);
        log.info("프로젝트 좋아요 수 추출");
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
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
            targetNameByProject.put(projectId, target.getTargetTitle());
        }

        return targetNameByProject;
    }

    private Map<Long, LikeInfo> getLikeInfoByProjectIds(final Long memberId, final List<Long> projectIds) {
        final Map<Long, LikeInfo> likeInfoByProject = new HashMap<>();

        final List<Long> nonCachedProjectIds = new ArrayList<>();
        for (final Long projectId : projectIds) {
            final String key = generateLikeKey(projectId);
            if (TRUE.equals(redisTemplate.hasKey(key))) {
                likeInfoByProject.put(projectId, readLikeInfoFromCache(key, memberId));
            } else {
                nonCachedProjectIds.add(projectId);
            }
        }

        if (!nonCachedProjectIds.isEmpty()) {
            final List<LikeElement> likeElements = customLikeRepository.findLikeElementByProjectIds(nonCachedProjectIds);
            likeElements.addAll(getEmptyLikeElements(likeElements, nonCachedProjectIds));
            likeElements.forEach(this::storeLikeInCache);
            likeInfoByProject.putAll(new LikeElements(likeElements).toLikeInfo(memberId));
        }
        return likeInfoByProject;
    }

    private LikeInfo readLikeInfoFromCache(final String key, final Long memberId) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        final boolean isLike = TRUE.equals(opsForSet.isMember(key, memberId));
        final long count = Objects.requireNonNull(opsForSet.size(key)) - 1;
        return new LikeInfo(count, isLike);
    }

    private void storeLikeInCache(final LikeElement likeElement) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        final String key = generateLikeKey(likeElement.getProjectId());
        opsForSet.add(key, EMPTY_MARKER);
        final Set<Long> memberIds = likeElement.getMemberIds();
        if (!memberIds.isEmpty()) {
            opsForSet.add(key, likeElement.getMemberIds().toArray());
        }
        redisTemplate.expire(key, LIKE_TTL);
    }

    private List<LikeElement> getEmptyLikeElements(
            final List<LikeElement> likeElements,
            final List<Long> nonCachedProjectIds
    ) {
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
