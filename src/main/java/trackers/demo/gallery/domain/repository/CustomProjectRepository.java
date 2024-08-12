package trackers.demo.gallery.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import trackers.demo.project.domain.Project;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;

import java.util.List;

public interface CustomProjectRepository {

    Slice<Project> findProjectsAllByCondition(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final ReadProjectFilterCondition readProjectFilterCondition,
            final Pageable pageable
    );

    List<Project> findLikedProjects(final Long memberId, final Pageable pageable);

    List<Project> findMyProjects(final Long memberId, final Pageable pageable);
}
