package trackers.demo.project.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import trackers.demo.project.domain.Project;
import trackers.demo.project.dto.request.ReadProjectFilterCondition;
import trackers.demo.project.dto.request.ReadProjectSearchCondition;

public interface CustomProjectRepository {

    Slice<Project> findProjectsAllByCondition(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final ReadProjectFilterCondition readProjectFilterCondition,
            final Pageable pageable
    );
}
