package trackers.demo.admin;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import trackers.demo.admin.domain.AdminMember;
import trackers.demo.admin.domain.repository.AdminMemberRepository;
import trackers.demo.admin.domain.type.AdminType;
import trackers.demo.auth.AdminAuth;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.global.exception.RefreshTokenException;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;
import trackers.demo.loginv2.infrastructure.BearerAuthorizationExtractor;
import trackers.demo.loginv2.infrastructure.JwtProvider;

import java.util.Arrays;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static trackers.demo.admin.domain.type.AdminType.*;
import static trackers.demo.global.exception.ExceptionCode.*;

@RequiredArgsConstructor
@Component
public class AdminLoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String REFRESH_TOKEN = "refresh-token";

    private final JwtProvider jwtProvider;

    private final BearerAuthorizationExtractor extractor;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AdminMemberRepository adminMemberRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Long.class)
                .hasParameterAnnotation(AdminAuth.class);
    }

    @Override
    public Accessor resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(request == null){
            throw new BadRequestException(INVALID_REQUEST);
        }

        final String refreshToken = extractRefreshToken(request.getCookies());
        final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
        jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));

        final Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));

        final AdminMember adminMember = adminMemberRepository.findById(memberId)
                .orElseThrow(() -> new RefreshTokenException(INVALID_ADMIN_AUTHORITY));

        if(adminMember.getAdminType().equals(MASTER)){
            return Accessor.master(memberId);
        }

        if(adminMember.getAdminType().equals(ADMIN)){
            return Accessor.admin(memberId);
        }
        throw new RefreshTokenException(INVALID_ADMIN_AUTHORITY);
    }

    private String extractRefreshToken(final Cookie... cookies) {
        if(cookies == null){
            throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
        }
        return Arrays.stream(cookies)
                .filter(this::isValidRefreshToken)
                .findFirst()
                .orElseThrow(() -> new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN))
                .getValue();
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName()) &&
                refreshTokenRepository.existsByToken(cookie.getValue());
    }
}
