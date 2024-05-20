package trackers.demo.loginv2.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberTokens {

    private final String refreshToken;
    private final String accessToken;
}
