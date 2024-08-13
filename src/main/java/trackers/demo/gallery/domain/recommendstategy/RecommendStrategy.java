package trackers.demo.gallery.domain.recommendstategy;

import org.springframework.data.domain.Pageable;
import trackers.demo.project.domain.Project;

import java.util.List;

public interface RecommendStrategy {

    List<Project> recommend(Pageable pageable);

    boolean isType(RecommendType recommendType);

    String getTitle();
}
