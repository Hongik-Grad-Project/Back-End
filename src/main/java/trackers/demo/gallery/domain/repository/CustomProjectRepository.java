package trackers.demo.gallery.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import trackers.demo.gallery.dto.request.ReadProjectTagCondition;
import trackers.demo.project.domain.Project;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;

import java.util.Collection;
import java.util.List;

public interface CustomProjectRepository {

    Slice<Project> findProjectsAllByCondition(
            final ReadProjectFilterCondition readProjectFilterCondition,
            final Pageable pageable
    );

    Slice<Project> findProjectsAllByKeyword(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final Pageable pageable);

    Slice<Project> findProjectsAllByTags(
            final ReadProjectTagCondition readProjectTagCondition,
            final Pageable pageable);

    List<Project> findLikedProjects(final Long memberId);

    List<Project> findLikedProjects(final Long memberId, final Pageable pageable);

    List<Project> getMyRecentProjects(final Long memberId, final Pageable pageable);


}
