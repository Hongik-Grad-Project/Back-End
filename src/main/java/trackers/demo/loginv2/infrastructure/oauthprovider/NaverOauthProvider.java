package trackers.demo.loginv2.infrastructure.oauthprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Na;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import trackers.demo.global.exception.AuthException;
import trackers.demo.loginv2.domain.OauthAccessToken;
import trackers.demo.loginv2.domain.OauthProvider;
import trackers.demo.loginv2.domain.OauthUserInfo;
import trackers.demo.loginv2.infrastructure.oauthuserinfo.NaverUserInfo;

import java.util.Map;
import java.util.Optional;

import static trackers.demo.global.exception.ExceptionCode.INVALID_AUTHORIZATION_CODE;
import static trackers.demo.global.exception.ExceptionCode.NOT_SUPPORTED_OAUTH_SERVICE;

@Component
@Slf4j
public class NaverOauthProvider implements OauthProvider {
    private static final String PROVIDER_NAME = "naver";

    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String userUri;

    public NaverOauthProvider (
            @Value("${NAVER_CLIENT_ID}") final String clientId,
            @Value("${NAVER_CLIENT_SECRET}") final String clientSecret,
            @Value("${NAVER_REDIRECT_URL}") final String redirectUri,
            @Value("${NAVER_TOKEN_URI}") final String tokenUri,
            @Value("${NAVER_USER_INFO_URI}") final String userUri
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
    }

    @Override
    public boolean is(final String name) { return PROVIDER_NAME.equals(name); }

//    @Override
//    public OauthUserInfo getUserInfo(final String code) {
//        final String accessToken = requestAccessToken(code);
//        final HttpHeaders headers = new HttpHeaders();
//
//        log.info("accessToken={}", accessToken);
//
//        headers.setBearerAuth(accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseUser = restTemplate.exchange(
//                "https://openapi.naver.com/v1/nid/me",
//                HttpMethod.GET,
//                new HttpEntity<>(headers),
//                String.class
//        );
//
//        String userInfo = responseUser.getBody();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(userInfo);
//        log.info("jsonNode={}", jsonNode);
//
//        String response = jsonNode.get("response").toString();
//        log.info("response={}", response);
//
//        return null;
//    }


    @Override
    public OauthUserInfo getUserInfo(final String code) {
        final String accessToken = requestAccessToken(code);
        final HttpHeaders headers = new HttpHeaders();

        log.info("accessToken={}", accessToken);

        headers.setBearerAuth(accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseUser;

        try {
            responseUser = restTemplate.exchange(
                    "https://openapi.naver.com/v1/nid/me",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Error during API call: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve user info", e);
        }

        String userInfo = responseUser.getBody();
        ObjectMapper objectMapper = new ObjectMapper();


        try {
            JsonNode jsonNode = objectMapper.readTree(userInfo);
            log.info("jsonNode={}", jsonNode);

            JsonNode responseNode = jsonNode.get("response");
            log.info("responseNode={}", responseNode);
            String id = responseNode.get("id").asText();

            String email = responseNode.get("email").asText();
            log.info("email={}", email);

            return new NaverUserInfo(id, email);
        } catch (Exception e) {

            log.error("Error parsing user info: {}", e.getMessage());
            throw new RuntimeException("Failed to parse user info", e);
        }


    }

    private String requestAccessToken(final String code) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(clientId, clientSecret);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("-----------------------------------------------------------------------------------------------");
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        System.out.println("-----------------------------------------------------------------------------------------------");
        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, httpHeaders);
        final ResponseEntity<OauthAccessToken> accessTokenResponse = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OauthAccessToken.class
        );
        System.out.println("-----------------------------------------------------------------------------------------------");
        log.info("accessTokenResponse={}", accessTokenResponse);
        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> new AuthException(INVALID_AUTHORIZATION_CODE))
                .getAccessToken();
    }
}
