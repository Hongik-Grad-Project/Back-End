package trackers.demo.like.presentation;

import jakarta.persistence.Access;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.like.dto.LikeInfo;
import trackers.demo.like.dto.request.LikeRequest;
import trackers.demo.like.dto.response.LikeResponse;
import trackers.demo.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Slf4j
public class LikeController {

    private final LikeService likeService;

    @MemberOnly
    @PostMapping("/{projectId}/like")
    public ResponseEntity<LikeResponse> updateLikeStatus(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId,
            @RequestBody final LikeRequest likeRequest
    ) {
        log.info("memberId={}의 projectId={} 상태 변경 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        final LikeResponse likeResponse = likeService.update(accessor.getMemberId(), projectId, likeRequest);
        return ResponseEntity.ok().body(likeResponse);
    }

}
