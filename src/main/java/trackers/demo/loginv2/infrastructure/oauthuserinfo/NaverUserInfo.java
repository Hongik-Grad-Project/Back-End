package trackers.demo.loginv2.infrastructure.oauthuserinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.loginv2.domain.OauthUserInfo;
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NaverUserInfo implements OauthUserInfo {

    @JsonProperty("id")
    private String socialLoginId;

    @JsonProperty("email")
    private String email;

    @Override
    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
