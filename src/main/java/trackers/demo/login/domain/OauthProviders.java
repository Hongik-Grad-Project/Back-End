package trackers.demo.login.domain;

import org.springframework.stereotype.Component;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.ExceptionCode;

import java.util.List;

@Component
public class OauthProviders {

    private final List<OauthProvider> providers;

    public OauthProviders(final List<OauthProvider> providers){
        this.providers = providers;
    }

    // providerName에 해당하는 OauthProvider을 찾아 반환
    public OauthProvider mapping(final String providerName){
        return providers.stream()
                .filter(provider -> provider.is(providerName))
                .findFirst()
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_SUPPORTED_OAUTH_SERVICE));
    }
}
