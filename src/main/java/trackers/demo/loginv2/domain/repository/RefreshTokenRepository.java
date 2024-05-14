package trackers.demo.loginv2.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.loginv2.domain.RefreshToken;

// 추후에 레디스 사용: JpaRepository -> CRUDRepository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
