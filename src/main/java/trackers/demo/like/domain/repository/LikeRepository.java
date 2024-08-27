package trackers.demo.like.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import trackers.demo.like.domain.Likes;

import java.util.Set;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    @Modifying  // 데이터를 수정함을 나타내는 어노테이션
    @Query("DELETE FROM Likes WHERE projectId IN :projectIds")
    void deleteByProjectIds(final Set<Long> projectIds);
}
