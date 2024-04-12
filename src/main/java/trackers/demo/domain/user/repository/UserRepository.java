package trackers.demo.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trackers.demo.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByAuthkey(String authKey);
    User findByAuthkey(String authkey);
    Boolean existsByUsername(String username);
    User findByUsername(String username);
}
