package trackers.demo.project.fixture;

import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.domain.type.DonatedStatusType;

import java.time.LocalDate;
import java.util.List;

import static trackers.demo.integration.IntegrationFixture.MEMBER;

public class ProjectFixture {

    public static final Project DUMMY_PROJECT = new Project(
            1L,
            MEMBER,
            true,
            "열정있는 팀원을 원합니다.",
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 7, 2),
            "은퇴 후 사업 시작 안전하게!",
            "default-image.png",
            List.of("소제목1", "소제목2"),
            List.of("본문1", "본문2"),
            List.of("project-image1.png", "project-image2.png"),
            DonatedStatusType.NOT_DONATED,
            CompletedStatusType.COMPLETED,
            100000,
            false);
}
