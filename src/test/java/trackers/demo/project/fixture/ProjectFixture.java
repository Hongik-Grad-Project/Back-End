package trackers.demo.project.fixture;

import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.member.dto.response.LikeProjectResponse;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.util.List;

import static trackers.demo.integration.IntegrationFixture.MEMBER;

public class ProjectFixture {

    // 프로젝트 고정 객체
    public static final Project DUMMY_PROJECT_NOT_COMPLETED = new Project(
            1L,
            MEMBER,
            "중장년층 실업 문제",
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 7, 2),
            "은퇴 후 사업 시작 안전하게!",
            "default-image.png",
            List.of("소제목1", "소제목2"),
            List.of("본문1", "본문2"),
            List.of("project-image1.png", "project-image2.png"),
            CompletedStatusType.NOT_COMPLETED,
            false);

    public static final ProjectResponse MY_PROJECT_1 = new ProjectResponse(
            1L,
            "/images/project1.png",
            "프로젝트 1 제목",
            "프로젝트 1 요약",
            "사회 문제 해결",
            LocalDate.of(2024, 12, 31),
            CompletedStatusType.COMPLETED,
            true,
            100L
    );

    public static final ProjectResponse MY_PROJECT_2 = new ProjectResponse(
            2L,
            "/images/project2.png",
            "프로젝트 2 제목",
            "프로젝트 2 요약",
            "교육 문제 해결",
            LocalDate.of(2024, 11, 30),
            CompletedStatusType.NOT_COMPLETED,
            false,
            50L
    );

    // 나의 프로젝트 고정 데이터 (List<ProjectResponse>)
    public static final List<ProjectResponse> MY_PROJECTS = List.of(MY_PROJECT_1, MY_PROJECT_2);


    public static final LikeProjectResponse LIKE_PROJECT_1 = new LikeProjectResponse(
            1L,
            "아동",
            "프로젝트 1 제목",
            LocalDate.of(2024, 12, 31)
    );

    public static final LikeProjectResponse LIKE_PROJECT_2 = new LikeProjectResponse(
            3L,
            "실버세대",
            "프로젝트 3 제목",
            LocalDate.of(2025, 1, 15)
    );

    // 내가 응원한 프로젝트 고정 데이터 (List<LikeProjectResponse>)
    public static final List<LikeProjectResponse> LIKE_PROJECTS = List.of(LIKE_PROJECT_1, LIKE_PROJECT_2);



}
