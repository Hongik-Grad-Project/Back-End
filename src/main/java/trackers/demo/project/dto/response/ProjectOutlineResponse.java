package trackers.demo.project.dto.response;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.project.domain.Project;

import java.time.LocalDate;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ProjectOutlineResponse {

    private final Long projectId;

    private final String projectTarget;

    private final String summary;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final String projectTitle;

    private final String mainImagePath;


    public static ProjectOutlineResponse of(final Project project, final String targetName) {
        return new ProjectOutlineResponse(
                project.getId(),
                targetName,
                project.getSummary(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectTitle(),
                project.getMainImagePath()
        );
    }
}
