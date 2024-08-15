package trackers.demo.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.chat.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
