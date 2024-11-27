package trackers.demo.note.domain.repository;

import trackers.demo.note.domain.Note;

public interface CustomNoteRepository {

    Note findByChatRoomIdAndIsSummarized(final Long chatRoomId, final boolean isSummarized);
}
