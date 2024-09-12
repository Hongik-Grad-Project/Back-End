package trackers.demo.note.infrastructure.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import trackers.demo.note.domain.Note;

import static trackers.demo.chat.domain.QChatRoom.chatRoom;
import static trackers.demo.note.domain.QNote.note;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QuerydslNoteRepository {

    private final JPAQueryFactory queryFactory;

    public Note findByChatRoomIdAndIsSummarized(final Long chatRoomId, final boolean isSummarized) {
        return queryFactory
                .selectFrom(note)
                .leftJoin(chatRoom).on(chatRoom.note.id.eq(note.id))
                .where(chatRoom.id.eq(chatRoomId)
                        .and(chatRoom.isSummarized.eq(isSummarized)))
                .fetchOne();
    }
}
