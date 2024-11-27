package trackers.demo.login.infrastructure.oauthuserinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import trackers.demo.login.domain.OauthUserInfo;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GoogleUserInfo implements OauthUserInfo {

    @JsonProperty("id")
    private String socialLoginId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;


    @Override
    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getNickname(){return name;}

    @Override
    public String getEmail() {
        return email;
    }

}
