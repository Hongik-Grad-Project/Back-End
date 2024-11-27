package trackers.demo.gallery.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResponse {

    private final List<String> tags;

    public static TagResponse of(final List<String> tags) {
        return new TagResponse(tags);
    }
}
