package trackers.demo.loginv2.domain.repository;

import org.springframework.data.repository.CrudRepository;
import trackers.demo.loginv2.domain.RefreshToken;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
