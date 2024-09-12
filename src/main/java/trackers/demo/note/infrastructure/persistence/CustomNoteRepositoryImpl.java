package trackers.demo.note.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import trackers.demo.note.domain.Note;
import trackers.demo.note.domain.repository.CustomNoteRepository;

@Repository
@RequiredArgsConstructor
public class CustomNoteRepositoryImpl implements CustomNoteRepository {

    private final QuerydslNoteRepository querydslNoteRepository;

    @Override
    public Note findByChatRoomIdAndIsSummarized(final Long chatRoomId, final boolean isSummarized) {
        return querydslNoteRepository.findByChatRoomIdAndIsSummarized(chatRoomId, isSummarized);
    }
}
