package trackers.demo.login.infrastructure.oauthprovider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import trackers.demo.global.exception.AuthException;
import trackers.demo.login.domain.OauthAccessToken;
import trackers.demo.login.domain.OauthProvider;
import trackers.demo.login.domain.OauthUserInfo;
import trackers.demo.login.infrastructure.oauthuserinfo.KakaoUserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.*;
import static trackers.demo.global.exception.ExceptionCode.INVALID_AUTHORIZATION_CODE;
import static trackers.demo.global.exception.ExceptionCode.NOT_SUPPORTED_OAUTH_SERVICE;

@Component
public class KakaoOauthProvider implements OauthProvider {

    private static final String PROVIDER_NAME = "kakao";
    private static final String SECURE_RESOURCE = "secure_resource";
    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String userUri;

    public KakaoOauthProvider(
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") final String clientSecret,
            @Value("${spring.security.oauth2.client.provider.kakao.token-uri}") final String tokenUri,
            @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}") final String userUri,
            @Value("${server.serverAddress}") final String serverAddress
    ){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        if("localhost".equals(serverAddress)){
            this.redirectUri = "http://" + serverAddress + ":3000/login/oauth2/callback/" + PROVIDER_NAME;
        } else {
            this.redirectUri = "https://" + serverAddress + "/login/oauth2/callback/" + PROVIDER_NAME;
        }
        this.tokenUri = tokenUri;
        this.userUri = userUri;
    }

    @Override
    public boolean is(final String name) {
        return PROVIDER_NAME.equals(name);
    }

    @Override
    public OauthUserInfo getUserInfo(final String code) {
        // 엑세스 토큰 요청
        final String accessToken = requestAccessToken(code);

        // 엑세스 토큰을 사용해 Bearer 토큰 인증 방식 설정
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // HTTP 요청 엔티티 생성
        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity = new HttpEntity<>(headers);

        // 쿼리 파라미터 생성
        final Map<String, Boolean> queryParam = new HashMap<>();
        queryParam.put(SECURE_RESOURCE, TRUE);

        // HTTP 요청 보내기 (사용자 정보 요청)
        final ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                userUri,
                HttpMethod.GET,
                userInfoRequestEntity,
                KakaoUserInfo.class,
                queryParam
        );

        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new AuthException(NOT_SUPPORTED_OAUTH_SERVICE);

    }

    // 카카오 인증 서버로부터 액세스 토큰 요청
    private String requestAccessToken(String code) {
        final HttpHeaders httpHeaders = new HttpHeaders();

        // 웹 폼 데이터를 전송하기 위한 인코딩 타입
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        // HTTP 요청 엔티티 생성
        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, httpHeaders);

        // HTTP 요청 보내기
        final ResponseEntity<OauthAccessToken> accessTokenResponse = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OauthAccessToken.class
        );

        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> new AuthException(INVALID_AUTHORIZATION_CODE))
                .getAccessToken();
    }
}
