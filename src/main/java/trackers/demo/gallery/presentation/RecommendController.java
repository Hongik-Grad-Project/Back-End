package trackers.demo.gallery.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.auth.Auth;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.gallery.service.RecommendService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommends")
@Slf4j
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getRecommendProjects(
            @Auth final Accessor accessor
    ){
        log.info("추천 프로젝트 조회 요청이 들어왔습니다.");
        final List<ProjectResponse> projectResponses = recommendService.getRecommendProjects(accessor);
        return ResponseEntity.ok().body(projectResponses);
    }

}
