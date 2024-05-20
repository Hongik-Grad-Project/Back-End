package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.project.domain.ProjectSubject;

public interface ProjectSubjectRepository extends JpaRepository<ProjectSubject, Long> {
}
