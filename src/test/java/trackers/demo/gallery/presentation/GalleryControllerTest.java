package trackers.demo.gallery.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;
import trackers.demo.gallery.dto.response.ProjectDetailResponse;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.gallery.service.GalleryService;
import trackers.demo.global.ControllerTest;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;
import trackers.demo.project.dto.request.ProjectCreateOutlineRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;
import static trackers.demo.member.fixture.MemberFixture.DUMMY_MEMBER;
import static trackers.demo.project.fixture.ProjectFixture.DUMMY_PROJECT_NOT_COMPLETED;

@WebMvcTest(GalleryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class GalleryControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GalleryService galleryService;

    @BeforeEach
    void setUp(){
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    // 테스트용 더미 데이터
    private void makeProjectOutline() throws Exception{
        final ProjectCreateOutlineRequest projectCreateFirstRequest = new ProjectCreateOutlineRequest(
                "아동",
                "건강한 삶",
                LocalDate.of(2024, 6, 25),
                LocalDate.of(2024, 8, 25),
                "은퇴 후 사업 시작 안전하게"
        );

        final MockMultipartFile projectMainImage = new MockMultipartFile(
                "file",
                "projectMainImage.png",
                "multipart/form-data",
                "./src/test/resources/static/images/projectMainImage.png".getBytes()
        );

        final MockMultipartFile createRequest = new MockMultipartFile(
                "dto",
                null,
                "application/json",
                objectMapper.writeValueAsString(projectCreateFirstRequest).getBytes(UTF_8)
        );

        performPostRequest(projectMainImage, createRequest);
    }

    private void makeProjectBody() throws Exception{

        final ProjectCreateBodyRequest projectCreateSecondRequest = new ProjectCreateBodyRequest(
                List.of("소제목1", "소제목2"),
                List.of("본문1", "본문2"),
                List.of("태그1", "태그2", "태그3", "태그4", "태그5")
        );

        final MockMultipartFile projectImage1 = new MockMultipartFile(
                "files",
                "project1.jpg",
                "multipart/form-data",
                "./src/test/resources/static/images/project1.jpg".getBytes()
        );

        final MockMultipartFile projectImage2 = new MockMultipartFile(
                "files",
                "project2.png",
                "multipart/form-data",
                "./src/test/resources/static/images/project2.png".getBytes()
        );

        final MockMultipartFile createRequestFile = new MockMultipartFile(
                "dto",
                null,
                "application/json",
                objectMapper.writeValueAsString(projectCreateSecondRequest).getBytes(UTF_8)
        );

        performPostRequest(1L,projectImage1, projectImage2, createRequestFile);
    }

    private ResultActions performPostRequest(
            final MockMultipartFile projectMainImage,
            final MockMultipartFile createRequest
    ) throws Exception{
        return mockMvc.perform(multipart(POST,"/project/outline/save")
                .file(projectMainImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));
    }

    private ResultActions performPostRequest(
            final Long projectId,
            final MockMultipartFile projectImage1,
            final MockMultipartFile projectImage2,
            final MockMultipartFile createRequestFile
    ) throws Exception{
        return mockMvc.perform(multipart(POST, "/project/{projectId}/body/save", projectId)
                .file(createRequestFile)
                .file(projectImage1)
                .file(projectImage2)
                .contentType(MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));
    }

    private ResultActions performGetRequest(
            final Pageable pageable,
            final ReadProjectSearchCondition searchCondition,
            final ReadProjectFilterCondition filterCondition
    ) throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/gallery")
                .queryParam("page", String.valueOf(pageable.getPageNumber()))
                .queryParam("size", String.valueOf(pageable.getPageSize()))
                .queryParam("sortType", "new")
                .queryParam("title", searchCondition.getKeyword())
//                .queryParam("isDonated", String.valueOf(filterCondition.isDonated()))
                .queryParam("targets", filterCondition.getTargets().toArray(new String[0]))
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
    }

    private ResultActions performGetRequest(final Long projectId) throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/gallery/{projectId}", projectId)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
    }

    @DisplayName("조건에 알맞는 프로젝트를 모두 조회할 수 있다")
    @Test
    void getAllProjectsByCondition() throws Exception{
        // given
        makeProjectOutline();
        makeProjectBody();

        ProjectResponse dummyResponse = ProjectResponse.of(DUMMY_PROJECT_NOT_COMPLETED, "실버세대", false, 0L);
        List<ProjectResponse> dummyResponseList = List.of(dummyResponse);

        Pageable pageable = PageRequest.of(1, 8);
        Page<ProjectResponse> dummyPage = new PageImpl<>(dummyResponseList, pageable, dummyResponseList.size());

        when(galleryService.getAllProjectsByCondition(
                any(), any(), any(), any()))
                .thenReturn(dummyPage);

        ReadProjectSearchCondition searchCondition = new ReadProjectSearchCondition("");
        ReadProjectFilterCondition filterCondition = new ReadProjectFilterCondition(List.of("실버세대", "청소년"));

        // when
        final ResultActions resultActions = performGetRequest(pageable, searchCondition, filterCondition);

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (1부터 시작)"),
                                parameterWithName("size").description("한 페이지에 프로젝트 개수 (default: 8)"),
                                parameterWithName("sortType").description("정렬 타입: new(default), likeCount(좋아요 순), recentTime(최신 순), closingTime(종료임박 순)"),
                                parameterWithName("title").description("프로젝트 제목에 포함된 단어 검색"),
//                                parameterWithName("isDonated").description("모금 여부 (true/false)"),
                                parameterWithName("targets").description("프로젝트 대상 리스트 (null일 때, 전체 대상 검색)")
                        ),
                        responseFields(
                                fieldWithPath("content[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("content[].mainImagePath").type(JsonFieldType.STRING).description("프로젝트 대표 이미지").attributes(field("constraint", "이미지 경로")),
                                fieldWithPath("content[].projectTitle").type(JsonFieldType.STRING).description("프로젝트명").attributes(field("constraint", "문자열")),
                                fieldWithPath("content[].summary").type(JsonFieldType.STRING).description("사회문제 요약").attributes(key("constraint").value("문자열")),
                                fieldWithPath("content[].target").type(JsonFieldType.STRING).description("프로젝트 대상").attributes(key("constraint").value("문자열")),
                                fieldWithPath("content[].endDate").type(JsonFieldType.STRING).description("프로젝트 종료 날짜").attributes(key("constraint").value("yyyy-MM-dd")),
                                fieldWithPath("content[].completedStatusType").type(JsonFieldType.STRING).description("프로젝트 작성 완료 여부").attributes(key("constraint").value("완료 상태를 나타내는 enum 값")),
                                fieldWithPath("content[].isLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부").attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제")),
                                fieldWithPath("content[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수").attributes(field("constraint", "양의 정수")),

                                // 페이지와 관련된 필드 추가
                                subsectionWithPath("pageable").ignored(),
                                subsectionWithPath("sort").ignored(),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 요소 수"),
                                fieldWithPath("first").description("첫 페이지 여부"),
                                fieldWithPath("size").description("페이지당 요소 수"),
                                fieldWithPath("number").description("페이지 번호"),
                                fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                        )
                ))
                .andReturn();

        Map<String, Object> responseMap = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<Map<String, Object>>() {}
        );

        List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");
        assertThat(content).hasSize(1);

        // 첫 번째 프로젝트의 각 필드 값 검증
        Map<String, Object> project = content.get(0);
        assertThat(project.get("projectId")).isEqualTo(1);
        assertThat(project.get("mainImagePath")).isEqualTo("default-image.png");
        assertThat(project.get("projectTitle")).isEqualTo("은퇴 후 사업 시작 안전하게!");
        assertThat(project.get("summary")).isEqualTo("중장년층 실업 문제");
        assertThat(project.get("target")).isEqualTo("실버세대");
        assertThat(project.get("endDate")).isEqualTo("2024-07-02");
        assertThat(project.get("completedStatusType")).isEqualTo("NOT_COMPLETED");
        assertThat(project.get("isLike")).isEqualTo(false);
        assertThat(project.get("likeCount")).isEqualTo(0);

// 페이지네이션 관련 필드 값 검증
        assertThat(responseMap.get("totalElements")).isEqualTo(9);
        assertThat(responseMap.get("totalPages")).isEqualTo(2);
        assertThat(responseMap.get("size")).isEqualTo(8);
        assertThat(responseMap.get("number")).isEqualTo(1);  // 페이지 번호 (0부터 시작이므로 1이 두 번째 페이지)
        assertThat(responseMap.get("first")).isEqualTo(false);
        assertThat(responseMap.get("last")).isEqualTo(true);
        assertThat(responseMap.get("numberOfElements")).isEqualTo(1);
        assertThat(responseMap.get("empty")).isEqualTo(false);

//        final PageImpl<ProjectResponse> projectResponses = objectMapper.readValue(
//                mvcResult.getResponse().getContentAsString(),
//                new TypeReference<PageImpl<ProjectResponse>>() {}
//        );
//        assertThat(projectResponses).usingRecursiveComparison()
//                .isEqualTo(dummyPage);
    }

    @DisplayName("ProjectId로 단일 프로젝트를 조회한다.")
    @Test
    void getProject() throws Exception{
        // given
        makeProjectOutline();
        makeProjectBody();
        doNothing().when(galleryService).validateProjectByProjectId(anyLong());
        when(galleryService.getProjectDetail(any(), any()))
                .thenReturn(ProjectDetailResponse.projectDetail(
                        DUMMY_PROJECT_NOT_COMPLETED,
                        List.of("태그1", "태그2"),
                        "실버 세대",
                        false,
                        0L,
                        DUMMY_MEMBER));

        // when
        final ResultActions resultActions = performGetRequest(1L);

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("projectId")
                                        .description("프로젝트 ID")
                        ),
                        responseFields(
                                fieldWithPath("projectId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로젝트 ID")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("projectTarget")
                                        .type(JsonFieldType.STRING)
                                        .description("프로젝트 대상")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("startDate")
                                        .type(JsonFieldType.STRING)
                                        .description("프로젝트 시작 날짜")
                                        .attributes(key("constraint").value("yyyy-MM-dd")),
                                fieldWithPath("projectTitle")
                                        .type(JsonFieldType.STRING)
                                        .description("프로젝트명")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("likeCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("좋아요 수")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("mainImagePath")
                                        .type(JsonFieldType.STRING)
                                        .description("프로젝트 대표 이미지")
                                        .attributes(field("constraint", "이미지 경로")),
                                fieldWithPath("tagList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("프로젝트 태그 리스트")
                                        .attributes(key("constraint").value("1개 이상 10개 이하의 문자열(최대 100자)")),
                                fieldWithPath("subtitleList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("소제목 리스트")
                                        .attributes(key("constraint").value("1개 이상의 3개 이하의 문자열(최대 180자)")),
                                fieldWithPath("contentList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("본문 리스트")
                                        .attributes(key("constraint").value("1개 이상의 3개 이하의 문자열(최대 3000자)")),
                                fieldWithPath("projectImageList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("프로젝트 사진 리스트")
                                        .attributes(key("constraint").value("최대 10장의 사진 파일")),
                                fieldWithPath("memberName")
                                        .type(JsonFieldType.STRING)
                                        .description("이름")
                                        .attributes(field("constraint", "프로젝트 제안자 이름")),
                                fieldWithPath("memberEmail")
                                        .type(JsonFieldType.STRING)
                                        .description("이메일")
                                        .attributes(field("constraint", "프로젝트 제안자 이메일")),
                                fieldWithPath("memberIntro")
                                        .type(JsonFieldType.STRING)
                                        .description("한 줄 소개")
                                        .attributes(field("constraint", "프로젝트 제안자 한 줄 소개")),
                                fieldWithPath("like")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("좋아요 여부")
                                        .attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제"))
                        )
                )).andReturn();

        final ProjectDetailResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ProjectDetailResponse.class
        );
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(ProjectDetailResponse.projectDetail(
                        DUMMY_PROJECT_NOT_COMPLETED,
                        List.of("태그1", "태그2"),
                        "실버 세대",
                        false,
                        0L,
                        DUMMY_MEMBER));
    }

}
