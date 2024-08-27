package trackers.demo.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
