package trackers.demo.chat.presentation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.ChatResponse;
import trackers.demo.chat.service.ChatService;
import trackers.demo.global.ControllerTest;
import trackers.demo.loginv2.domain.MemberTokens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.chat.fixture.ChatFixture.DUMMY_CHAT_MESSAGE_RESPONSE;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;

@WebMvcTest(ChatController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class ChatControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    void setUp(){
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performCreateRoomPostRequestV1() throws Exception {
        return mockMvc.perform(post("/chat/v1")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions performCreateRoomPostRequestV2() throws Exception {
        return mockMvc.perform(post("/chat/v2")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions performCreateMessagePostRequestV1(final CreateMessageRequest createMessageRequest) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/chat/{chatRoomId}/message/v1", 1)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMessageRequest))
        );
    }

    private ResultActions performCreateMessagePostRequestV2(final CreateMessageRequest createMessageRequest) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/chat/{chatRoomId}/message/v2", 1)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMessageRequest))
        );
    }

    @DisplayName("새로운 채팅방을 만들 수 있다.")
    @Test
    void createChatRoomV1() throws Exception{
        // given
        when(chatService.createRoomV1(anyLong())).thenReturn(1L);

        // when
        final ResultActions resultActions = performCreateRoomPostRequestV1();

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseHeaders(
                                headerWithName(LOCATION).description("생성된 채팅방 URL")
                        )
                ));

    }

    @DisplayName("오로라AI에게 메시지를 보낼 수 있다.")
    @Test
    void createMessageV1() throws Exception{
        // given
        final CreateMessageRequest createMessageRequest =
                new CreateMessageRequest("안녕, 사회 문제에 대해 이야기 하고 싶어");

        when(chatService.createMessageV1(anyLong(), anyLong(), any(CreateMessageRequest.class)))
                .thenReturn(DUMMY_CHAT_MESSAGE_RESPONSE);

        // when
        final ResultActions resultActions = performCreateMessagePostRequestV1(createMessageRequest);

        // then
        final MvcResult mvcResult =  resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("responseMessage").type(JsonFieldType.STRING).description("오로라 AI 답변").attributes(field("constraint", "문자열"))
                        )
                )).andReturn();

        final ChatResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ChatResponse.class
        );

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(DUMMY_CHAT_MESSAGE_RESPONSE);
    }

    @DisplayName("새로운 채팅방을 만들 수 있다.")
    @Test
    void createChatRoomV2() throws Exception{
        // given
        when(chatService.createRoomV2(anyLong())).thenReturn(1L);

        // when
        final ResultActions resultActions = performCreateRoomPostRequestV2();

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseHeaders(
                                headerWithName(LOCATION).description("생성된 채팅방 URL")
                        )
                ));

    }

    @DisplayName("오로라AI에게 메시지를 보낼 수 있다.")
    @Test
    void createMessageV2() throws Exception{
        // given
        final CreateMessageRequest createMessageRequest =
                new CreateMessageRequest("안녕, 사회 문제에 대해 이야기 하고 싶어");

        when(chatService.createMessageV2(anyLong(), anyLong(), any(CreateMessageRequest.class)))
                .thenReturn(DUMMY_CHAT_MESSAGE_RESPONSE);

        // when
        final ResultActions resultActions = performCreateMessagePostRequestV2(createMessageRequest);

        // then
        final MvcResult mvcResult =  resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("responseMessage").type(JsonFieldType.STRING).description("오로라 AI 답변").attributes(field("constraint", "문자열"))
                        )
                )).andReturn();

        final ChatResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ChatResponse.class
        );

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(DUMMY_CHAT_MESSAGE_RESPONSE);
    }

}
