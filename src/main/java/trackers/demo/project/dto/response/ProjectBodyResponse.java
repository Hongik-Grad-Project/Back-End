package trackers.demo.project.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.project.domain.Project;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ProjectBodyResponse {

    private final Long projectId;

    private final List<String> subtitleList;

    private final List<String> contentList;

    private final List<String> projectImageList;

    private final List<String> tagList;

    public static ProjectBodyResponse of(final Project project, final List<String> tagList) {

        return new ProjectBodyResponse(
                project.getId(),
                project.getSubTitleList(),
                project.getContentList(),
                project.getProjectImageList(),
                tagList
        );
    }
}
