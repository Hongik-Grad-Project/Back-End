package trackers.demo.project.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.repository.CustomProjectRepository;
import trackers.demo.project.dto.request.ReadProjectSearchCondition;

@Repository
@RequiredArgsConstructor
public class CustomProjectRepositoryImpl implements CustomProjectRepository {

    private final QuerydslProjectRepository querydslProjectRepository;
    @Override
    public Slice<Project> findProjectsAllByCondition(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final Pageable pageable) {
        return querydslProjectRepository.findProjectsAllByCondition(readProjectSearchCondition, pageable);
    }
}
