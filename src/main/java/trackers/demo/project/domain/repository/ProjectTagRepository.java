package trackers.demo.project.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTag;
import trackers.demo.project.domain.Tag;

import java.util.List;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {

    List<ProjectTag> findAllByProject(final Project project);

    void deleteByProjectAndTag(final Project project, final Tag tag);

    void deleteAllByProjectId(final Long projectId);

    @Modifying
    @Query("""
            UPDATE ProjectTag projectTag
            SET projectTag.status = 'DELETED'
            WHERE projectTag.project.id IN :projectIds
            """
    )
    void deleteAllByProjectIds(@Param("projectIds") final List<Long> projectIds);

}
