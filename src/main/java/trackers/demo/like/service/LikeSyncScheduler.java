package trackers.demo.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.like.domain.LikeRedisConstants;
import trackers.demo.like.domain.Likes;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.domain.repository.LikeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static trackers.demo.like.domain.LikeRedisConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeSyncScheduler {

    private final LikeRepository likeRepository;

    private final CustomLikeRepository customLikeRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0 * * * *")
    public void writeBackLikeCache(){
        // 레디스에서 "like:"로 시작하는 모든 키를 조회
        log.info("동기화 진행");
        final Set<String> likeKeys = redisTemplate.keys(LIKE_KEY_PREFIX + WILD_CARD);
        if(Objects.isNull(likeKeys)){
            return;
        }

        final Set<Long> projectIds = extractProjectIdsInRedisKeys(likeKeys);
        likeRepository.deleteByProjectIds(projectIds);

        final List<Likes> likes = extractLikesInRedisValues(projectIds);
        customLikeRepository.saveAll(likes);
    }

    private Set<Long> extractProjectIdsInRedisKeys(final Set<String> likeKeys) {
        return likeKeys.stream().map(key -> {
            final int indexOfColon = key.indexOf(KEY_SEPARATOR);
            return Long.valueOf(key.substring(indexOfColon+1));
        }).collect(Collectors.toSet());
    }

    private List<Likes> extractLikesInRedisValues(final Set<Long> projectIds) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        return projectIds.stream()
                .flatMap(projectId -> {
                    final String key = generateLikeKey(projectId);
                    final Set<Object> memberIds = opsForSet.members(key);
                    return Optional.ofNullable(memberIds)
                            .map(ids -> ids.stream()
                                    .filter(memberId -> !EMPTY_MARKER.equals(memberId))
                                    .map(memberId -> new Likes(projectId, (Long) memberId))
                            ).orElseGet(Stream::empty);
                }).toList();
    }
}
