package trackers.demo.loginv2.infrastructure.oauthprovider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.loginv2.domain.OauthAccessToken;
import trackers.demo.loginv2.domain.OauthProvider;
import trackers.demo.loginv2.domain.OauthUserInfo;
import trackers.demo.loginv2.infrastructure.oauthuserinfo.GoogleUserInfo;

import java.util.Optional;

import static trackers.demo.global.exception.ExceptionCode.*;

@Component
public class GoogleOauthProvider implements OauthProvider {

    private static final String PROVIDER_NAME = "google";

    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String userUri;

    public GoogleOauthProvider(
            @Value("${spring.security.oauth2.client.registration.google.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.google.client-secret}") final String clientSecret,
            @Value("${spring.security.oauth2.client.provider.google.token-uri}") final String tokenUri,
            @Value("${spring.security.oauth2.client.provider.google.user-info-uri}") final String userUri,
            @Value("${server.serverAddress}") final String serverAddress
    ){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = "http://" + ("localhost".equals(serverAddress) ? "localhost" : serverAddress) + ":3000/login/oauth2/callback/google";
        this.tokenUri = tokenUri;
        this.userUri = userUri;
    }
    @Override
    public boolean is(final String name) {
        return PROVIDER_NAME.equals(name);
    }

    @Override
    public OauthUserInfo getUserInfo(final String code) {
        final String accessToken = requestAccessToken(code);
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // HTTP 요청 엔티티 생성
        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity = new HttpEntity<>(headers);
        // HTTP 요청 보내기
        final ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                userUri,
                HttpMethod.GET,
                userInfoRequestEntity,
                GoogleUserInfo.class
        );

        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new AuthException(NOT_SUPPORTED_OAUTH_SERVICE);

    }

    // 구글 인증 서버로부터 액세스 토큰 요청
    private String requestAccessToken(String code) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(clientId, clientSecret);
        // 웹 폼 데이터를 전송하기 위한 인코딩 타입
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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
