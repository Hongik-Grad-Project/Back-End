package trackers.demo.admin.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.admin.dto.request.AdminLoginRequest;
import trackers.demo.admin.service.AdminLoginService;
import trackers.demo.auth.AdminAuth;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.loginv2.dto.AccessTokenResponse;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminLoginController {

    private static final int COOKIE_AGE_SECONDS = 604800;

    private final AdminLoginService adminLoginService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @RequestBody @Valid final AdminLoginRequest adminLoginRequest,
            final HttpServletResponse response
            ){
        final MemberTokens memberTokens = adminLoginService.login(adminLoginRequest);
        final ResponseCookie cookie = ResponseCookie.from("refresh-token", memberTokens.getRefreshToken())
                .maxAge(COOKIE_AGE_SECONDS)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(memberTokens.getAccessToken()));
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendLogin(
            @CookieValue("refresh-token") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ){
        final String renewalRefreshToken = adminLoginService.renewalAccessToken(refreshToken, authorizationHeader);
        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(renewalRefreshToken));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @AdminAuth final Accessor accessor,
            @CookieValue("refresh-token") final String refreshToken){
        adminLoginService.removeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
