package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.project.domain.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
