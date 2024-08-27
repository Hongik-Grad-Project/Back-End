package trackers.demo.gallery.domain.recommendstategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.repository.ProjectRepository;

import java.util.List;

import static trackers.demo.gallery.domain.recommendstategy.RecommendType.LIKE;

@Component
@RequiredArgsConstructor
public class LikesRecommendStrategy implements RecommendStrategy{

    private static final String TITLE =  "지금 인기있는 프로젝트들이에요";

    private final ProjectRepository projectRepository;

    @Override
    public List<Project> recommend(final Pageable pageable) {
        return projectRepository.findProjectsOrderByLikesCount(pageable);
    }

    @Override
    public boolean isType(final RecommendType recommendType) {
        return LIKE.equals(recommendType);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
