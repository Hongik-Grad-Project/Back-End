package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTarget;

public interface ProjectTargetRepository extends JpaRepository<ProjectTarget,Long> {

    ProjectTarget findByProject(Project project);
}
