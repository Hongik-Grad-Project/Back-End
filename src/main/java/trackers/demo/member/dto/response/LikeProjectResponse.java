package trackers.demo.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import trackers.demo.project.domain.Project;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LikeProjectResponse {

    private final Long projectId;

    private final String target;

    private final String projectTitle;

    private final LocalDate endDate;

    public static LikeProjectResponse of(final Project project, final String targetTitle) {

        return new LikeProjectResponse(
                project.getId(),
                targetTitle,
                project.getProjectTitle(),
                project.getEndDate()
        );

    }
}
