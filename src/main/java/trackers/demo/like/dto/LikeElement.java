package trackers.demo.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

@Getter
@AllArgsConstructor
public class LikeElement {

    private final Long projectId;

    private final long likeCount;

    private final Set<Long> memberIds;

    public boolean isLike(final Long memberId) {
        return memberIds.contains(memberId);
    }

    public static LikeElement empty(final Long projectId){
        return new LikeElement(projectId, 0, Collections.emptySet());
    }
}
