package trackers.demo.note.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.note.domain.Note;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectProposalResponse {

    private Long projectId;
    private String target;
    private String summary;
    private String projectTitle;
    private List<String> subTitleList;
    private List<String> contentList;

    public static ProjectProposalResponse of(
            final Long projectId,
            final AutomatedProposalResponse automatedProposalResponse,
            final Note note) {
        return new ProjectProposalResponse(
                projectId,
                note.getTarget(),
                note.getProblem(),
                note.getTitle(),
                automatedProposalResponse.getTitleList(),
                automatedProposalResponse.getContentList()
        );
    }
}
