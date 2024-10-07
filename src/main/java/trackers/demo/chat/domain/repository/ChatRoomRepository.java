package trackers.demo.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trackers.demo.chat.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c WHERE c.member.id = :memberId ORDER BY c.updatedAt DESC")
    List<ChatRoom> findByMemberId(final Long memberId);

    Boolean existsByMemberIdAndId(final Long memberId, final Long chatRoomId);

    Optional<ChatRoom> findByNoteId(final Long noteId);

    List<ChatRoom> findByMemberIdAndIsSummarized(final Long memberId, final boolean isSummarized);

    Optional<ChatRoom> findByMemberIdAndId(final Long memberId, final Long chatRoomId);

    @Query("SELECT c.id FROM ChatRoom c WHERE c.member.id = :memberId")
    List<Long> findChatRoomIdsByMemberId(final Long memberId);
}
