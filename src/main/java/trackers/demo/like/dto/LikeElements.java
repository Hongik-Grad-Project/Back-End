package trackers.demo.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class LikeElements {

    private final List<LikeElement> elements;

    public Map<Long, LikeInfo> toLikeInfo(final Long memberId){
        final Map<Long, LikeInfo> map = new HashMap<>();
        for(final LikeElement likeElement : elements){
            final LikeInfo likeInfo = new LikeInfo(likeElement.getLikeCount(), likeElement.isLike(memberId));
            map.put(likeElement.getProjectId(), likeInfo);
        }
        return map;
    }
}
