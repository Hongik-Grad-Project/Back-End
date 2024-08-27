//package trackers.demo.loginv1.oauth;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.UriComponentsBuilder;
//import trackers.demo.loginv1.jwt.JWTUtil;
//import trackers.demo.loginv2.domain.RefreshToken;
//import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;
//import trackers.demo.loginv1.oauth.dto.CustomOAuth2User;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Iterator;
//
//@Component
//@RequiredArgsConstructor
//public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JWTUtil jwtUtil;
//
//    private final RefreshTokenRepository refreshTokenRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        //OAuth2User(유저 정보) authKey와 role을 가져옴
//        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
//
//        String authKey = customUserDetails.getAuthKey();
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//
//        // 다중 토큰 발급
//        String access = jwtUtil.createJwt("access", authKey, role, 600000L);
//        String refresh = jwtUtil.createJwt("refresh", authKey, role, 86400000L);
//
//        // Refresh 토큰 저장
//        addRefreshEntity(authKey, refresh, 86400000L);
//
//        // 응답 설정
//        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/").toUriString();
//        response.addHeader("access", access);
//        response.addCookie(createCookie("refresh", refresh));
//        response.setStatus(HttpStatus.OK.value());
//        response.sendRedirect(redirectUrl);
//    }
//
//    private void addRefreshEntity(String authkey, String refresh, Long expiredMs) {
//
//        Date date = new Date(System.currentTimeMillis() + expiredMs);
//
//        RefreshToken refreshTokenEntity = RefreshToken.builder()
//                .authkey(authkey)
//                .refresh(refresh)
//                .expiration(date.toString())
//                .build();
//
//        refreshTokenRepository.save(refreshTokenEntity);
//    }
//
//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(24*60*60);
//        //cookie.setSecure(true);   // 오직 HTTPS 통신에서만 쿠키 사용
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);   // javascript가 쿠키를 가져가지 못함
//
//        return cookie;
//    }
//}
