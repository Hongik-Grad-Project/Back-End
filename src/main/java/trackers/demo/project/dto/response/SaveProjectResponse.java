package trackers.demo.project.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class SaveProjectResponse {

    private final Long projectId;

    private final boolean isSave;

    public static SaveProjectResponse of(final Long projectId, final boolean isSave) {
        return new SaveProjectResponse(
                projectId, isSave
        );
    }
}
