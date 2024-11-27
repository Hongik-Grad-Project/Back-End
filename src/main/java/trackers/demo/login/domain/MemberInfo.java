package trackers.demo.login.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.member.domain.Member;

@Getter
@RequiredArgsConstructor
public class MemberInfo {

    private final Member member;
    private final String refreshToken;
    private final String accessToken;
}
