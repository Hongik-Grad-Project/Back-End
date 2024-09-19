package trackers.demo.member.fixture;

import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.MemberState;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static trackers.demo.integration.IntegrationFixture.MEMBER;

public class MemberFixture {

    public static final Member DUMMY_MEMBER = new Member(
            1L,
            "test_0001",
            "member1@test.com",
            "테스트용 사용자",
            "default-image.png",
            "한 줄 소개가 아직 입력되지 않았습니다",
            MemberState.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
    );

}
