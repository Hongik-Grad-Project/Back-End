package trackers.demo.like.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.dto.LikeElement;
import trackers.demo.like.dto.request.LikeRequest;

import java.util.Set;

import static trackers.demo.like.domain.LikeRedisConstants.*;
import static java.lang.Boolean.FALSE;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final CustomLikeRepository customLikeRepository;

    public void update(final Long memberId, final Long projectId, final LikeRequest likeRequest) {
        final String key = generateLikeKey(projectId);
        if(FALSE.equals(redisTemplate.hasKey(key))){
            final LikeElement likeElement = customLikeRepository.findLikesElementByProjectId(projectId)
                    .orElse(LikeElement.empty(projectId));
            storeLikeInCache(likeElement);
        }
        updateToCache(key, memberId, likeRequest.getIsLike());
    }

    private void storeLikeInCache(final LikeElement likeElement) {
        // 레디스의 Set 자료구조
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        final String key = generateLikeKey(likeElement.getProjectId());
        opsForSet.add(key, EMPTY_MARKER);
        final Set<Long> memberIds = likeElement.getMemberIds();
        if(!memberIds.isEmpty()){
            opsForSet.add(key, likeElement.getMemberIds().toArray());
        }
        redisTemplate.expire(key, LIKE_TTL);
    }

    private void updateToCache(final String key, final Long memberId, final Boolean islike) {
        if(islike){
            addMember(key, memberId);
            return;
        }
        removeMember(key, memberId);
    }

    private void addMember(final String key, final Long memberId) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        opsForSet.add(key, memberId);
        redisTemplate.expire(key, LIKE_TTL);
    }

    private void removeMember(final String key, final Long memberId) {
        final SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        opsForSet.remove(key, memberId);
        redisTemplate.expire(key, LIKE_TTL);
    }
}
