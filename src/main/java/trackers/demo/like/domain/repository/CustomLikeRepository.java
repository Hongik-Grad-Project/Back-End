package trackers.demo.like.domain.repository;

import trackers.demo.like.domain.Likes;
import trackers.demo.like.dto.LikeElement;

import java.util.List;
import java.util.Optional;

public interface CustomLikeRepository {

    void saveAll(final List<Likes> likes);

    Optional<LikeElement> findLikesElementByProjectId(final Long projectId);
}
