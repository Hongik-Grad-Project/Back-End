package trackers.demo.like.domain;

import java.time.Duration;

public class LikeRedisConstants {

    public static final String LIKE_KEY_PREFIX = "like:";

    // 특정 패턴을 가진 모든 키를 찾고자 할 때 사용
    public static final String WILD_CARD = "*";
    public static final String KEY_SEPARATOR = ":";

    public static final Long EMPTY_MARKER = -1L;

    // Redis 캐시에서 90분 후에 만료
    public static final Duration LIKE_TTL = Duration.ofMinutes(90L);

    public static String generateLikeKey(final Long projectId){
        return LIKE_KEY_PREFIX + projectId;
    }
}
