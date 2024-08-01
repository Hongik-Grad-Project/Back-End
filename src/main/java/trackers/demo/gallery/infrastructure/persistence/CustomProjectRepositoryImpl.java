package trackers.demo.gallery.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import trackers.demo.project.domain.Project;
import trackers.demo.gallery.domain.repository.CustomProjectRepository;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;

@Repository
@RequiredArgsConstructor
public class CustomProjectRepositoryImpl implements CustomProjectRepository {

    private final QuerydslProjectRepository querydslProjectRepository;
    @Override
    public Slice<Project> findProjectsAllByCondition(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final ReadProjectFilterCondition readProjectFilterCondition,
            final Pageable pageable) {
        return querydslProjectRepository.findProjectsAllByCondition(
                readProjectSearchCondition,
                readProjectFilterCondition,
                pageable);
    }
}