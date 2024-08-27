package trackers.demo.gallery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectResponse {

    private final Long projectId;

    private final String mainImagePath;

    private final String projectTitle;

    private final String summary;

    private final String target;

    private final LocalDate endDate;

    private final CompletedStatusType completedStatusType;

    private final Boolean isLike;

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
                project.getCompletedStatus(),
                isLike,
                likeCount
        );
    }
}
