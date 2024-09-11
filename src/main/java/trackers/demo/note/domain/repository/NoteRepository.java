package trackers.demo.note.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.note.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Note findByChatRoomId(final Long chatRoomId);
}
