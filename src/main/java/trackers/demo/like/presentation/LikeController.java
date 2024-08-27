package trackers.demo.like.presentation;

import jakarta.persistence.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.like.dto.request.LikeRequest;
import trackers.demo.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class LikeController {

    private final LikeService likeService;

    @MemberOnly
    @PostMapping("/{projectId}/like")
    public ResponseEntity<Void> updateLikeStatus(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId,
            @RequestBody final LikeRequest likeRequest
    ) {
       likeService.update(accessor.getMemberId(), projectId, likeRequest);
       return ResponseEntity.noContent().build();
    }


}
