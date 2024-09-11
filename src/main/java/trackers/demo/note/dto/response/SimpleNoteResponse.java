package trackers.demo.note.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.note.domain.Note;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleNoteResponse {

    private Long noteId;
    private String title;
    private LocalDateTime createdAt;

    public static SimpleNoteResponse of(final Note note) {
        return new SimpleNoteResponse(note.getId(), note.getTitle(), note.getCreatedAt());
    }
}
