package trackers.demo.gallery.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import trackers.demo.gallery.domain.repository.CustomProjectTagRepository;
import trackers.demo.project.domain.Tag;
import trackers.demo.project.domain.repository.ProjectTagRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProjectTagRepositoryImpl implements CustomProjectTagRepository {

    private final QuerydslProjectTagRepository querydslProjectTagRepository;

    @Override
    public List<Tag> findPopularTagsByCount(final int count) {
        return querydslProjectTagRepository.findPopularTagsByCount(count);
    }
}
