package trackers.demo.login.domain.repository;

import org.springframework.data.repository.CrudRepository;
import trackers.demo.login.domain.RefreshToken;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
