package trackers.demo.note.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.note.domain.Note;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetailNoteResponse {

    private Long noteId;
    private String target;
    private String problem;
    private String title;
    private List<String> openTitleList;
    private List<String> openSummaryList;
    private String solution;

    public static DetailNoteResponse of(final Note note) {
        return new DetailNoteResponse(
                note.getId(),
                note.getTarget(),
                note.getProblem(),
                note.getTitle(),
                note.getOpenTitleList(),
                note.getOpenSummaryList(),
                note.getSolution()
        );
    }
}
