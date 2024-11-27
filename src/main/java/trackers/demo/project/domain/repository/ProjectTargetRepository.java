package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTarget;

import java.util.List;

public interface ProjectTargetRepository extends JpaRepository<ProjectTarget,Long> {

    ProjectTarget findByProjectId(final Long projectId);

    void deleteByProjectId(final Long projectId);

    @Modifying
    @Query("""
            UPDATE ProjectTarget projectTarget
            SET projectTarget.status = 'DELETED'
            WHERE projectTarget.project.id IN :projectIds
            """)
    void deleteByProjectIds(@Param("projectIds") final List<Long> projectIds);
}
