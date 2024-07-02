package trackers.demo.integration;

import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.MemberState;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IntegrationFixture {

    /* Member */
    public static final Member MEMBER = new Member(
            1L,
            "socialLoginId",
            "test@email.com"
    );
}
