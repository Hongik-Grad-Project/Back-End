package trackers.demo.loginv2.domain;

import org.springframework.web.client.RestTemplate;

public interface OauthProvider {

    // 스프링에서 제공하는 HTTP 통신을 간편하게 수행하기 위함
    RestTemplate restTemplate = new RestTemplate();

    boolean is(String name);

    OauthUserInfo getUserInfo(String code);
}
