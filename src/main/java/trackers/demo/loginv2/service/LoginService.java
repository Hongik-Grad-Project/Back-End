package trackers.demo.loginv2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.loginv2.domain.*;
import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;
import trackers.demo.loginv2.infrastructure.BearerAuthorizationExtractor;
import trackers.demo.loginv2.infrastructure.JwtProvider;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;

import static trackers.demo.global.exception.ExceptionCode.FAIL_TO_VALIDATE_TOKEN;
import static trackers.demo.global.exception.ExceptionCode.INVALID_REFRESH_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private static final int MAX_TRY_COUNT = 5;
    private static final int FOUR_DIGIT_RANGE = 10000;

    private final BearerAuthorizationExtractor bearerExtractor;

    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public MemberTokens login(final String providerName,final String code) {
        // OAuth Provider 가져오기
        final OauthProvider provider = oauthProviders.mapping(providerName);
        // 사용자 정보 가져오기
        final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);
        // 사용자 생성 혹은 조회
        final Member member = findOrCreateMember(
                oauthUserInfo.getSocialLoginId(),
                oauthUserInfo.getNickname(),
                oauthUserInfo.getEmail());
        // 로그인 토큰 생성
        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());
        // refresh 토큰 저장
        log.info("Access Token: {}, Refresh Token {}", memberTokens.getAccessToken(), memberTokens.getRefreshToken());
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);
        // 로그인 토큰 반환
        return memberTokens;
    }

    private Member findOrCreateMember(final String socialLoginId, final String nickname, final String email) {
        return memberRepository.findBySocialLoginId(socialLoginId)  // 조회 성공
                .orElseGet(() -> createMember(socialLoginId, nickname, email));   // 새로운 멤버 생성
    }

    private Member createMember(final String socialLoginId, final String nickname, final String email) {
        // todo: 랜덤 이름 생성
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT){
            final String nicknameWithRandomNumber = nickname + generateRandomFourDigitCode();
            if(!memberRepository.existsByNickname(nicknameWithRandomNumber)){
                return memberRepository.save(new Member(socialLoginId, nicknameWithRandomNumber ,email));
            }
            tryCount += 1;
        }
        throw new AuthException(ExceptionCode.FAIL_TO_CREATE_NEW_MEMBER);
    }

    private String generateRandomFourDigitCode() {
        final int randomNumber = (int) (Math.random() * FOUR_DIGIT_RANGE);
        return String.format("%04d", randomNumber);
    }

    public String renewalAccessToken(String refreshTokenRequest, String authorizationHeader) {
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);
        // 리프레시 토큰이 유효하고 엑세스 토근이 무효한 경우
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

    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    public void deleteAccount(Long memberId) {
        // 해당 멤버 관련 데이터 삭제: 프로젝트 삭제, 공감하기, 등등
        memberRepository.deleteByMemberId(memberId);
    }

}
