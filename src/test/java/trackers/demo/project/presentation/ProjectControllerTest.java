package trackers.demo.project.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import trackers.demo.global.ControllerTest;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.dto.request.ProjectCreateOutlineRequest;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.time.LocalDate;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;

@WebMvcTest(ProjectController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class ProjectControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ImageService imageService;

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

    private ResultActions performPostRequest(
            final MockMultipartFile projectMainImage,
            final MockMultipartFile createRequest
            ) throws Exception{
        return mockMvc.perform(multipart(POST,"/project/first")
                .file(projectMainImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));
    }

    private ResultActions performPostRequest(
            final MockMultipartFile projectImage1,
            final MockMultipartFile projectImage2,
            final MockMultipartFile createRequestFile
    ) throws Exception{
        return mockMvc.perform(multipart(POST, "/project/second")
                .file(createRequestFile)
                .file(projectImage1)
                .file(projectImage2)
                .contentType(MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));
    }

    @DisplayName("프로젝트를 임시 저장(생성)할 수 있다.")
    @Test
    void saveProjectOutline() throws Exception{
        // given
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

        // when
        final ResultActions resultActions = performPostRequest(projectMainImage, createRequest);

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                                requestCookies(
                                  cookieWithName("refresh-token")
                                          .description("갱신 토큰")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization")
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)"))
                                ),
                                requestParts(
                                        partWithName("dto").description("프로젝트 생성 객체"),
                                        partWithName("file").description("프로젝트 대표 사진. 지원되는 형식은 .png, .jpg 등이 있습니다.")
                                ),
                                requestPartFields("dto",
                                        fieldWithPath("target")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 대상")
                                                .attributes(key("constraint").value("문자열")),
                                        fieldWithPath("subject")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 주제")
                                                .attributes(key("constraint").value("문자열")),
                                        fieldWithPath("isRecruit")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀원 모집 여부")
                                                .attributes(key("constraint").value("true: 모집 희망, false: 모집 안함")),
                                        fieldWithPath("wantedMember")
                                                .type(JsonFieldType.STRING)
                                                .description("희망 팀원")
                                                .attributes(key("constraint").value("문자열")),
                                        fieldWithPath("startDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 시작 날짜")
                                                .attributes(key("constraint").value("yyyy-MM-dd")),
                                        fieldWithPath("endDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 종료 날짜")
                                                .attributes(key("constraint").value("yyyy-MM-dd")),
                                        fieldWithPath("projectTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 제목")
                                                .attributes(key("constraint").value("문자열"))
                                )
                        )
                );

//        resultActions.andExpect(status().isBadRequest())
//                .andDo(print()); // 출력하여 응답 본문을 확인

    }

    @DisplayName("프로젝트 등록을 완료할 수 있다.")
    @Test
    void saveProjectBody() throws Exception{
        // given
        makeProjectOutline();
        doNothing().when(projectService).validateProjectByMemberAndProjectId(anyLong(), anyLong(), any(CompletedStatusType.class));

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

        // when
        final ResultActions resultActions = performPostRequest(projectImage1, projectImage2, createRequestFile);

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        requestParts(
                                partWithName("dto").description("프로젝트 생성 객체"),
                                partWithName("files").description("프로젝트 사진 리스트. 지원되는 형식은 .png, .jpg 등이 있습니다")
                        ),
                        requestPartFields("dto",
                                fieldWithPath("subtitleList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("소제목 리스트")
                                        .attributes(key("constraint").value("1개 이상의 문자열(최대 200자)")),
                                fieldWithPath("contentList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("본문 리스트")
                                        .attributes(key("constraint").value("1개 이상의 문자열(최대 2000자)"))
                                ),
                        responseHeaders(
                                headerWithName(LOCATION).description("생성된 프로젝트 URL")
                        )
                ));
    }

}