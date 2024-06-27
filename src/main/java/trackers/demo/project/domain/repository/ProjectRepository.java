package trackers.demo.project.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.member.domain.Member;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Project findByMemberAndCompletedStatus(final Member member, final CompletedStatusType completedStatus);

    Boolean existsByMemberIdAndCompletedStatus(final Long memberId, final CompletedStatusType completedStatusType);
}
