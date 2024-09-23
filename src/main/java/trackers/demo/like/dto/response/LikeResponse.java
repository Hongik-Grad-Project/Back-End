package trackers.demo.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private Long likeCount;
    private boolean isLike;

    public static LikeResponse of(final Long likeCount, final boolean isLike) {
        return new LikeResponse(likeCount, isLike);
    }
}
