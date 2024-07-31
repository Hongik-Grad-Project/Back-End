package trackers.demo.gallery.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.project.domain.Project;

import java.time.LocalDate;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ProjectResponse {

    private final Long projectId;

    private final String mainImagePath;

    private final String projectTitle;

    private final String summary;

    private final String target;

    private final LocalDate endDate;

    private final Boolean islike;

    private final Long likeCount;

    public static ProjectResponse of(
            final Project project,
            final String target,
            final Boolean isLike,
            final Long likeCount) {
        return new ProjectResponse(
                project.getId(),
                project.getMainImagePath(),
                project.getProjectTitle(),
                project.getSummary(),
                target,
                project.getEndDate(),
                isLike,
                likeCount
        );
    }
}
