package trackers.demo.member.presentation;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.like.service.LikeSyncScheduler;
import trackers.demo.member.dto.request.MyProfileUpdateRequest;
import trackers.demo.member.dto.response.LikeProjectResponse;
import trackers.demo.member.dto.response.MyPageResponse;
import trackers.demo.member.service.MemberService;
import trackers.demo.project.service.ImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;
    private final LikeSyncScheduler likeSyncScheduler;

    @GetMapping
    @MemberOnly
    public ResponseEntity<MyPageResponse> getMyInfo(@Auth final Accessor accessor){
        log.info("memberId={}의 마이페이지 조회 요청이 들어왔습니다.", accessor.getMemberId());
        memberService.validateProfileByMember(accessor.getMemberId());
        likeSyncScheduler.writeBackLikeCache();
        final MyPageResponse myPageResponse = memberService.getMyPageInfo(accessor.getMemberId());
        return ResponseEntity.ok().body(myPageResponse);
    }

    @PostMapping("/update")
    @MemberOnly
    public ResponseEntity<Void> updateMyProfile(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final MyProfileUpdateRequest updateRequest,
            @RequestPart(value = "file",required = false) MultipartFile profileImage
    ) {
        log.info("memberId={}의 프로필 수정 요청이 들어왔습니다.", accessor.getMemberId());
        memberService.validateProfileByMember(accessor.getMemberId());

        String imageUrl = null;
        if(profileImage != null && !profileImage.isEmpty()){
            imageUrl = imageService.saveImage(profileImage);
        }

        memberService.updateProfile(accessor.getMemberId(), updateRequest, imageUrl);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project")
    @MemberOnly
    public ResponseEntity<List<ProjectResponse>> getMyProjects(@Auth final Accessor accessor){
        log.info("memberId={}의 내 프로젝트 모두 조회 요청이 들어왔습니다.", accessor.getMemberId());
        memberService.validateProfileByMember(accessor.getMemberId());
        final List<ProjectResponse> myProjectsResponse = memberService.getMyProjects(accessor.getMemberId());
        return ResponseEntity.ok().body(myProjectsResponse);
    }

    @GetMapping("/like")
    @MemberOnly
    ResponseEntity<List<LikeProjectResponse>> getLikeProjects(@Auth final Accessor accessor){
        log.info("memberId={}의 좋아요한 프로젝트를 모두 조회했습니다.", accessor.getMemberId());
        memberService.validateProfileByMember(accessor.getMemberId());
        final List<LikeProjectResponse> likeProjectsResponse = memberService.getLikeProjects(accessor.getMemberId());
        return ResponseEntity.ok().body(likeProjectsResponse);
    }
}
