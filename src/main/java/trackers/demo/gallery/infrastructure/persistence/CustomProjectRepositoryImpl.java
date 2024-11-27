package trackers.demo.gallery.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import trackers.demo.gallery.dto.request.ReadProjectTagCondition;
import trackers.demo.project.domain.Project;
import trackers.demo.gallery.domain.repository.CustomProjectRepository;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProjectRepositoryImpl implements CustomProjectRepository {

    private final QuerydslProjectRepository querydslProjectRepository;

    @Override
    public Slice<Project> findProjectsAllByCondition(
            final ReadProjectFilterCondition readProjectFilterCondition,
            final Pageable pageable) {
        return querydslProjectRepository.findProjectsAllByCondition(readProjectFilterCondition, pageable);
    }

    @Override
    public Slice<Project> findProjectsAllByKeyword(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final Pageable pageable) {
        return querydslProjectRepository.findProjectsAllByKeyword(readProjectSearchCondition, pageable);
    }

    @Override
    public Slice<Project> findProjectsAllByTags(
            final ReadProjectTagCondition readProjectTagCondition,
            final Pageable pageable) {
        return querydslProjectRepository.findProjectsAllByTags(readProjectTagCondition, pageable);
    }

    @Override
    public List<Project> findLikedProjects(Long memberId) {
        return querydslProjectRepository.findLikedProjects(memberId);
    }

    @Override
    public List<Project> findLikedProjects(final Long memberId, final Pageable pageable) {
        return querydslProjectRepository.findLikedProjects(memberId, pageable);
    }

    @Override
    public List<Project> getMyRecentProjects(final Long memberId, final Pageable pageable) {
        return querydslProjectRepository.getMyRecentProjects(memberId, pageable);
    }


}
