package trackers.demo.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.chat.domain.ChatRoom;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByMemberId(final Long memberId);

    Boolean existsByMemberIdAndId(final Long memberId, final Long chatRoomId);
}
