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
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

    protected MockMultipartFile getMockMultipartFile() {
        String name = "projectMainImage";
        String contentType = "multipart/form-data";
        String path = "./src/test/resources/static/images/projectMainImage.png";

        return new MockMultipartFile(name, path, contentType, path.getBytes(UTF_8));
    }


    // 테스트용 더미 데이터
    private void makeProject() throws Exception{
        final ProjectCreateFirstRequest projectCreateFirstRequest = new ProjectCreateFirstRequest(
                "아동",
                "건강한 삶",
                true,
                "열정있는 팀원을 원합니다.",
                LocalDate.of(2024, 6, 25),
                LocalDate.of(2024, 8, 25),
                "은퇴 후 사업 시작 안전하게"
        );

        performPostRequest(projectCreateFirstRequest);
    }

    private ResultActions performPostRequest(
            final ProjectCreateFirstRequest projectCreateFirstRequest
            ) throws Exception{
        return mockMvc.perform(post("/project/first")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectCreateFirstRequest)));
    }

    @DisplayName("프로젝트를 임시 저장(생성)할 수 있다.")
    @Test
    void createProjectFirst() throws Exception{
        // given
        final ProjectCreateFirstRequest projectCreateFirstRequest = new ProjectCreateFirstRequest(
                "아동",
                "건강한 삶",
                true,
                "열정있는 팀원을 원합니다.",
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
        final ResultActions resultActions = mockMvc.perform(multipart(POST, "/project/first")
                .file(projectMainImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

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
}
