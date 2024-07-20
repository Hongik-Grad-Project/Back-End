package trackers.demo.integration;

import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.MemberState;
import trackers.demo.project.domain.Subject;
import trackers.demo.project.domain.Target;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.dto.request.ProjectCreateSecondRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IntegrationFixture {

    /* Member */
    public static final Member MEMBER = new Member(
            1L,
            "socialLoginId",
            "test@email.com"
    );

    /* Target */
    public static final Target TARGET = new Target(1L, "아동");

    /* Subject */
    public static final Subject SUBJECT = new Subject(1L, "모두의 교육");


    /* Project */
    public static final ProjectCreateFirstRequest PROJECT_CREATE_FIRST_REQUEST = new ProjectCreateFirstRequest(
            "아동",
            "모두의 교육",
            true,
            "열정있는 팀원을 원합니다",
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 8, 1),
            "모든 아동을 위한 교육"
    );

    public static final ProjectCreateSecondRequest PROJECT_CREATE_SECOND_REQUEST = new ProjectCreateSecondRequest(
            List.of("소제목1", "소제목2"),
            List.of("본문1", "본문2")
    );

}
