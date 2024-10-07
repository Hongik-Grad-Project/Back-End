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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.gallery.service.RecommendService;
import trackers.demo.global.ControllerTest;
import trackers.demo.login.domain.MemberTokens;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;
import static trackers.demo.project.fixture.ProjectFixture.*;

@WebMvcTest(RecommendController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class RecommendControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendService recommendService;

    @BeforeEach
    void setUp(){
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetRecommendRequest() throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/recommends")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
    }

    @DisplayName("추천 프로젝트를 조회할 수 있다.")
    @Test
    void getRecommendProjects() throws Exception{
        // given
        when(recommendService.getRecommendProjects(any()))
                .thenReturn(RECOMMEND_PROJECTS);

        // when
        final ResultActions resultActions = performGetRecommendRequest();

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].mainImagePath").type(JsonFieldType.STRING).description("프로젝트 대표 이미지").attributes(field("constraint", "이미지 경로")),
                                fieldWithPath("[].projectTitle").type(JsonFieldType.STRING).description("프로젝트명").attributes(field("constraint", "문자열")),
                                fieldWithPath("[].summary").type(JsonFieldType.STRING).description("사회문제 요약").attributes(key("constraint").value("문자열")),
                                fieldWithPath("[].target").type(JsonFieldType.STRING).description("프로젝트 대상").attributes(key("constraint").value("문자열")),
                                fieldWithPath("[].endDate").type(JsonFieldType.STRING).description("프로젝트 종료 날짜").attributes(key("constraint").value("yyyy-MM-dd")),
                                fieldWithPath("[].completedStatusType").type(JsonFieldType.STRING).description("프로젝트 작성 완료 여부").attributes(key("constraint").value("완료 상태를 나타내는 enum 값")),
                                fieldWithPath("[].isLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부").attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제")),
                                fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수").attributes(field("constraint", "양의 정수"))
                        )
                ))
                .andReturn();

        final List<ProjectResponse> projectResponses = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<ProjectResponse>>() {}
        );
        assertThat(projectResponses).usingRecursiveComparison()
                .isEqualTo(RECOMMEND_PROJECTS);
    }
}
