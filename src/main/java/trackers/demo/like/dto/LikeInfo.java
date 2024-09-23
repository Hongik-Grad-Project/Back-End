package trackers.demo.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class LikeInfo {

    private final long likeCount;

    private final boolean isLike;
}
