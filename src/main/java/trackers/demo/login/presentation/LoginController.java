package trackers.demo.login.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.login.domain.MemberInfo;
import trackers.demo.login.dto.request.AgreementRequest;
import trackers.demo.login.dto.response.AccessTokenResponse;
import trackers.demo.login.dto.request.LoginRequest;
import trackers.demo.login.dto.response.LoginResponse;
import trackers.demo.login.service.LoginService;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    public static final int COOKIE_AGE_SECONDS = 604800;

    private final LoginService loginService;

    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody final LoginRequest loginRequest,    // code
            final HttpServletResponse response
    ){
        log.info("로그인 요청이 들어왔습니다.");
        final MemberInfo memberInfo = loginService.login(provider, loginRequest.getCode());
        final Boolean isFirstLogin = loginService.checkFirstLogin(memberInfo.getMember());
        final ResponseCookie cookie = ResponseCookie.from("refresh-token", memberInfo.getRefreshToken())
                .maxAge(COOKIE_AGE_SECONDS)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
        return ResponseEntity.status(CREATED).body(
                new LoginResponse(memberInfo.getAccessToken(), isFirstLogin)
        );
    }

    @PostMapping("/login/terms")
    @MemberOnly
    public ResponseEntity<Void> checkServiceTerms(
            @Auth final Accessor accessor,
            @RequestBody final AgreementRequest agreementRequest
    ) {
        log.info("memberId={}의 이용약관 동의 여부 요청이 들어왔습니다.", accessor.getMemberId());
        loginService.checkServiceTerms(accessor.getMemberId(), agreementRequest);
        return ResponseEntity.noContent().build();
    }

    // 토큰 재발행 (로그인 연장)
    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendLogin(
            @CookieValue("refresh-token") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ){
        final String renewalAccessToken = loginService.renewalAccessToken(refreshToken, authorizationHeader);
        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(renewalAccessToken));
    }

    @DeleteMapping("/logout")
    @MemberOnly
    public ResponseEntity<Void> logout(
            @Auth final Accessor accessor,
            @CookieValue("refresh-token") final String refreshToken){
        log.info("memberId={}의 로그아웃 요청이 들어왔습니다.", accessor.getMemberId());
        loginService.removeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account")
    @MemberOnly
    public ResponseEntity<Void> deleteAccount(@Auth final Accessor accessor){
        log.info("memberId={}의 회원 탈퇴 요청이 들어왔습니다.", accessor.getMemberId());
        loginService.deleteAccount(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
