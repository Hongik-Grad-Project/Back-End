package trackers.demo.project.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.util.List;


public interface ProjectRepository extends JpaRepository<Project, Long>{


    Boolean existsByMemberIdAndIdAndCompletedStatus(
            final Long memberId,
            final Long projectId,
            final CompletedStatusType completedStatusType
    );

    Boolean existsByMemberIdAndId(final Long memberId, final Long projectId);

    @Query("""
            SELECT project FROM Project project
            LEFT JOIN Likes likes ON likes.projectId = project.id
            WHERE project.completedStatus = 'COMPLETED'
            GROUP BY project.id
            ORDER BY COUNT(likes.projectId) DESC
            """)
    List<Project> findProjectsOrderByLikesCount(final Pageable pageable);

    List<Project> findProjectsByMemberId(final Long memberId);

    List<Project> findByEndDateBeforeAndCompletedStatus(final LocalDate today, final CompletedStatusType completedStatusType);

    @Query("SELECT p.id FROM Project p WHERE p.member.id = :memberId")
    List<Long> findProjectIdsByMemberId(final Long memberId);

    @Modifying
    @Query("""
            UPDATE Project project
            SET project.status = 'DELETED'
            WHERE project.id IN :projectIds
            """)
    void deleteByProjectIds(@Param("projectIds") final List<Long> projectIds);
}
