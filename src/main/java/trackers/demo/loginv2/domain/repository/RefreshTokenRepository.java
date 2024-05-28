package trackers.demo.loginv2.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.loginv2.domain.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

// 추후에 레디스 사용: JpaRepository -> CrudRepository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshToken);

    boolean existsByToken(String refreshToken);

    void deleteByCreatedAtBefore(LocalDateTime time);
}
