package trackers.demo.login.infrastructure;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.ExpiredPeriodJwtException;
import trackers.demo.global.exception.InvalidJwtException;
import trackers.demo.login.domain.MemberTokens;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static trackers.demo.global.exception.ExceptionCode.EXPIRED_PERIOD_ACCESS_TOKEN;
import static trackers.demo.global.exception.ExceptionCode.INVALID_ACCESS_TOKEN;

@Component
public class JwtProvider {

    public static final String EMPTY_SUBJECT = "";

    private final SecretKey secretKey;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;

    public JwtProvider(
            @Value("${spring.jwt.secret}") final String secretKey,
            @Value("${spring.jwt.access-expiration-time}") final Long accessExpirationTime,
            @Value("${spring.jwt.refresh-expiration-time}") final Long refreshExpirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public MemberTokens generateLoginToken(final String subject){
        final String refreshToken = createToken(EMPTY_SUBJECT, refreshExpirationTime);
        final String accessToken = createToken(subject, accessExpirationTime);
        return new MemberTokens(refreshToken, accessToken);
    }

    private String createToken(final String subject, final Long validityInMilliseconds) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String regenerateAccessToken(final String subject) {     // subject = memberId
        return createToken(subject, accessExpirationTime);
    }

    public void validateTokens(final MemberTokens memberTokens){
        validateRefreshToken(memberTokens.getRefreshToken());
        validateAccessToken(memberTokens.getAccessToken());
    }

    private void validateRefreshToken(final String refreshToken) {
        try {
            parseToken(refreshToken);
        } catch (final ExpiredJwtException e){
            throw new ExpiredPeriodJwtException(ExceptionCode.EXPIRED_PERIOD_REFRESH_TOKEN);
        } catch (final JwtException | IllegalArgumentException e){
            throw new InvalidJwtException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void validateAccessToken(final String accessToken) {
        try {
            parseToken(accessToken);
        } catch (final ExpiredJwtException e) {
            throw new ExpiredPeriodJwtException(EXPIRED_PERIOD_ACCESS_TOKEN);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
        }
    }

    public boolean isValidRefreshAndInvalidAccess(final String refreshToken, final String accessToken) {
        validateRefreshToken(refreshToken);
        try{
            validateAccessToken(accessToken);
        } catch (final ExpiredPeriodJwtException e){
            return true;
        }
        return false;
    }

    public boolean isValidRefreshAndValidAccess(final String refreshToken, final String accessToken){
        try {
            validateRefreshToken(refreshToken);
            validateAccessToken(accessToken);
            return true;
        } catch (final JwtException e){
            return false;
        }
    }

    // JWT 문자열을 파싱하여 그 안에 포함된 클레임(헤더, 페이로드, 서명)을 추출
    private Jws<Claims> parseToken(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }


    public String getSubject(String token) {
        return parseToken(token)
                .getPayload()
                .getSubject();
    }
}
