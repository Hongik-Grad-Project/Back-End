package trackers.demo.member.fixture;

import trackers.demo.member.domain.Member;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.util.List;

import static trackers.demo.integration.IntegrationFixture.MEMBER;

public class MemberFixture {

    public static final Member DUMMY_MEMBER = new Member(
            1L,
            "test_0001",
            "테스트용 사용자",
            "member1@test.com"
    );

}
