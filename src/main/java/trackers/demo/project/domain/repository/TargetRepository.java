package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.project.domain.Target;

import java.util.Optional;

public interface TargetRepository extends JpaRepository<Target, Long> {

    Optional<Target> findByTargetTitle(String targetName);
}
