package trackers.demo.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.admin.domain.AdminMember;
import trackers.demo.admin.domain.repository.AdminMemberRepository;
import trackers.demo.admin.dto.request.AdminLoginRequest;
import trackers.demo.admin.infrastructure.PasswordEncoder;
import trackers.demo.global.exception.AdminException;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.loginv2.domain.RefreshToken;
import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;
import trackers.demo.loginv2.infrastructure.BearerAuthorizationExtractor;
import trackers.demo.loginv2.infrastructure.JwtProvider;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLoginService {
    private final AdminMemberRepository adminMemberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProvider jwtProvider;

    private final BearerAuthorizationExtractor bearerExtractor;

    private final PasswordEncoder passwordEncoder;
    public MemberTokens login(final AdminLoginRequest adminLoginRequest) {
        final AdminMember adminMember = adminMemberRepository
                .findByUsername(adminLoginRequest.getUsername())
                .orElseThrow(() -> new AdminException(INVALID_USER_NAME));

        if(passwordEncoder.matches(adminLoginRequest.getPassword(), adminMember.getPassword())){
            final MemberTokens memberTokens = jwtProvider.generateLoginToken(adminMember.getId().toString());
            final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), adminMember.getId());
            refreshTokenRepository.save(savedRefreshToken);
            return memberTokens;
        }

        throw new AdminException(INVALID_PASSWORD);
    }

    public String renewalAccessToken(String refreshTokenRequest, String authorizationHeader) {
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);
        if(jwtProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)){
            final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                    .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
            return jwtProvider.regenerateAccessToken(refreshToken.getMemberId().toString());
        }
        if(jwtProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)){
            return accessToken;
        }
        throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
    }

    public void removeRefreshToken(final String refreshToken){
        refreshTokenRepository.deleteById(refreshToken);
    }
}
