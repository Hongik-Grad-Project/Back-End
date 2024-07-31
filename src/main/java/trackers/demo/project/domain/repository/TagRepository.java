package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.project.domain.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Boolean existsByTagTitle(final String tagTitle);

    Optional<Tag> findByTagTitle(String tagTitle);
}
