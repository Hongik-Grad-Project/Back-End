package trackers.demo.like.presentation;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import trackers.demo.global.ControllerTest;
import trackers.demo.like.dto.LikeInfo;
import trackers.demo.like.dto.request.LikeRequest;
import trackers.demo.like.service.LikeService;
import trackers.demo.loginv2.domain.MemberTokens;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;
import static trackers.demo.like.fixture.LikeFixture.DUMMY_LIKE_RESPONSE;

@WebMvcTest(LikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class LikeControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LikeService likeService;

    @BeforeEach
    void setUp(){
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performPostRequest(final LikeRequest request) throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.post("/project/{projectId}/like", 1)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @DisplayName("게시물의 좋아요 여부를 변경할 수 있다.")
    @Test
    void updateLikeStatus() throws Exception{
        // given
        final LikeRequest likeRequest = new LikeRequest(false);
        when(likeService.update(anyLong(), anyLong(), eq(likeRequest)))
                .thenReturn(DUMMY_LIKE_RESPONSE);

        // when
        final ResultActions resultActions = performPostRequest(likeRequest);

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("projectId").description("프로젝트 ID")
                        ),
                        requestFields(
                                fieldWithPath("isLike").type(JsonFieldType.BOOLEAN).description("변경할 좋아요 상태값").attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제"))
                        ),
                        responseFields(
                                fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("isLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부").attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제"))
                        )
                )).andReturn();

        final LikeInfo likeInfo = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                LikeInfo.class
        );
        assertThat(likeInfo).usingRecursiveComparison().isEqualTo(DUMMY_LIKE_RESPONSE);
    }
}
