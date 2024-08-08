package trackers.demo.loginv2.infrastructure.oauthuserinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import trackers.demo.loginv2.domain.OauthUserInfo;

import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class KakaoUserInfo implements OauthUserInfo {

    @JsonProperty("id")
    private String socialLoginId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Override
    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getNickname() { return kakaoAccount.kakaoProfile.nickname; }

    @Override
    public String getEmail() { return kakaoAccount.email; }

    @NoArgsConstructor(access = PRIVATE)
    private static class KakaoAccount{
        @JsonProperty("profile")
        private KakaoProfile kakaoProfile;

        @JsonProperty("email")
        private String email;
    }

    @NoArgsConstructor(access = PRIVATE)
    private static class KakaoProfile {
        @JsonProperty("nickname")
        private String nickname;
    }

}
