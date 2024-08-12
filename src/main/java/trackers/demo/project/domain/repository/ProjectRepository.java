package trackers.demo.project.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trackers.demo.member.domain.Member;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.util.List;


public interface ProjectRepository extends JpaRepository<Project, Long>{


    Boolean existsByMemberIdAndIdAndCompletedStatus(final Long memberId, final Long projectId, final CompletedStatusType completedStatusType);

    @Query("""
            SELECT project FROM Project project
            LEFT JOIN Likes likes ON likes.projectId = project.id
            WHERE project.completedStatus = 'COMPLETED'
            GROUP BY project.id
            ORDER BY COUNT(likes.projectId) DESC
            """)
    List<Project> findProjectsOrderByLikesCount(final Pageable pageable);
}
