package trackers.demo.admin.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.admin.domain.Assistant;

import java.util.Optional;

public interface AssistantRepository extends JpaRepository<Assistant, Long> {

    Optional<Assistant> findByName(final String auroraAiChatBot);
}
