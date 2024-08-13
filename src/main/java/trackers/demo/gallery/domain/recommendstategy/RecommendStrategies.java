package trackers.demo.gallery.domain.recommendstategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.InvalidDomainException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendStrategies {

    private final List<RecommendStrategy> recommendStrategies;

    public RecommendStrategy mapByRecommendType(final RecommendType recommendType){
        return recommendStrategies.stream()
                .filter(recommendStrategy -> recommendStrategy.isType(recommendType))
                .findFirst()
                .orElseThrow(() -> new InvalidDomainException(ExceptionCode.NOT_FOUND_RECOMMEND_PROJECT_STRATEGY));
    }
}
