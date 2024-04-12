package trackers.demo.domain.volunteer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.domain.volunteer.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Boolean existsByName(String name);
    Organization findByName(String name);
}
