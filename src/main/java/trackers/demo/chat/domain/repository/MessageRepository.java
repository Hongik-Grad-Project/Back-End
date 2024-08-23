package trackers.demo.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.chat.domain.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    boolean existsByChatRoomId(final Long chatRoomId);

    List<Message> findAllByChatRoomId(final Long chatRoomId);
}
