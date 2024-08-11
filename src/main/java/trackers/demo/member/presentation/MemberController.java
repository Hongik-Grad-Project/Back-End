package trackers.demo.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.member.dto.request.MyProfileUpdateRequest;
import trackers.demo.member.service.MemberService;
import trackers.demo.project.service.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my-profile")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;

    // todo: 마이페이지 반환 (API: GET)

    @PostMapping("/update")
    @MemberOnly
    public ResponseEntity<Void> updateMyProfile(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final MyProfileUpdateRequest updateRequest,
            @RequestPart(value = "file",required = false) MultipartFile profileImage
    ) {
        log.info("memberId={}의 미니 프로필 수정 요청이 들어왔습니다.", accessor.getMemberId());
        memberService.validateProfileByMember(accessor.getMemberId());

        String imageUrl = null;
        if(profileImage != null && !profileImage.isEmpty()){
            // todo : 프로필 이미지는 S3의 "/profile" 경로에 저장하기
            imageUrl = imageService.saveImage(profileImage);
        }

        memberService.updateProfile(accessor.getMemberId(), updateRequest, imageUrl);

        return ResponseEntity.noContent().build();
    }

    // todo: 내 프로젝트 전체 조회 (API: GET /project)

    // todo: 응원 프로젝트 전체 조회 (API: GET /project/like)
}
