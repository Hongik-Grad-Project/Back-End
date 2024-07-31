package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTag;

import java.util.List;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {

    List<ProjectTag> findAllByProject(final Project project);
}
