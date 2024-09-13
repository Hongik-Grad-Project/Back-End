package trackers.demo.note.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trackers.demo.note.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Note findByChatRoomId(final Long chatRoomId);

    @Modifying
    @Query("UPDATE Note n SET n.status = 'DELETED' WHERE n.id = :noteId")
    void deleteById(final Long noteId);

}
