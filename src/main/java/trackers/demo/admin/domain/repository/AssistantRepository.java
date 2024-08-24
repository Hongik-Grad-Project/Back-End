package trackers.demo.admin.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.admin.domain.Assistant;

public interface AssistantRepository extends JpaRepository<Assistant, Long> {

    Assistant findByName(final String auroraAiChatBot);
}
