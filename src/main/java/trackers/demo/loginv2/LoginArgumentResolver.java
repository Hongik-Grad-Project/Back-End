package trackers.demo.loginv2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import trackers.demo.auth.Auth;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.RefreshTokenException;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;
import trackers.demo.loginv2.infrastructure.BearerAuthorizationExtractor;
import trackers.demo.loginv2.infrastructure.JwtProvider;

import java.util.Arrays;

import static org.springframework.http.HttpHeaders.*;

/*
* HTTP 요청 파라미터를 컨트롤러 메서드의 파라미터로 매핑
* */
@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String REFRESH_TOKEN = "refresh-token";
    private final JwtProvider jwtProvider;
    private BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;

    // Resolver가 어떤 파라미터를 처리할 것인지
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {

        // Long 타입의 파라미터 중에서 @Auth 어노테이션이 있는 경우 true 반환
        return parameter.withContainingClass(Long.class)
                .hasParameterAnnotation(Auth.class);
    }

    // 파라미터 해석
    @Override
    public Accessor resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(request == null){
            throw new BadRequestException(ExceptionCode.INVALID_REQUEST);
        }
        try{
            final String refreshToken = extractRefreshToken(request.getCookies());
            final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
            jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));

            final Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
            return Accessor.member(memberId);
        } catch (final RefreshTokenException e){
            return Accessor.guest();
        }
    }

    private String extractRefreshToken(final Cookie... cookies) {
        if(cookies == null){
            throw new RefreshTokenException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN);
        }
        return Arrays.stream(cookies)
                .filter(this::isValidRefreshToken)
                .findFirst()
                .orElseThrow(() -> new RefreshTokenException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN))
                .getValue();
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName()) &&
                refreshTokenRepository.existsByToken(cookie.getValue());
    }
}
