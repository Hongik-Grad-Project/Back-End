package trackers.demo.admin.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.admin.domain.AdminMember;
import trackers.demo.admin.domain.type.AdminType;

import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<AdminMember, Long> {

    Optional<AdminMember> findByUsername(String username);

    Boolean existsByIdAndAdminType(Long id, AdminType adminType);

    Boolean existsByUsername(String username);
}
