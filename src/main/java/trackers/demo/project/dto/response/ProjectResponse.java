package trackers.demo.project.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.project.domain.Project;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ProjectResponse {

    private final Long projectId;

    private final String mainImagePath;

    private final String projectTitle;

    private final int likes;

    public static ProjectResponse of(final Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getMainImagePath(),
                project.getProjectTitle(),
                project.getLikes()
        );
    }
}
