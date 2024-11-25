package trackers.demo.chat.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
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
import trackers.demo.chat.dto.response.ChatRoomResponse;
import trackers.demo.chat.service.ChatService;
import trackers.demo.global.ControllerTest;
import trackers.demo.login.domain.MemberTokens;
import trackers.demo.note.dto.response.DetailNoteResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.chat.fixture.ChatFixture.*;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;
import static trackers.demo.note.fixture.NoteFixture.DUMMY_DETAIL_NOTE_RESPONSE;

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

    private ResultActions performGetChatRoomsRequest() throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/chat")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
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

    private ResultActions performCreateNotePostRequestV1() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/chat/{chatRoomId}/summary/v1", 1)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions performCreateNotePostRequestV2() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/chat/{chatRoomId}/summary/v2", 1)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.delete("/chat/{chatRoomId}", 1)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performGetChatHistoryResponse() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/chat/{chatRoomId}/history", 1)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions performGetNoteDetailRequest() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/chat/{chatRoomId}/note", 1)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON));
    }

    @DisplayName("채팅방 리스트를 조회할 수 있다.")
    @Test
    void getChatRooms() throws Exception {
        // given
        when(chatService.getChatRooms(anyLong())).thenReturn(List.of(DUMMY_CHAT_ROOM_1, DUMMY_CHAT_ROOM_2, DUMMY_CHAT_ROOM_3));

        // when
        final ResultActions resultActions = performGetChatRoomsRequest();

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseFields(
                                fieldWithPath("[].chatRoomId").type(JsonFieldType.NUMBER).description("채팅방 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].chatRoomName").type(JsonFieldType.STRING).description("채팅방 이름").attributes(field("constraint", "문자열")),
                                fieldWithPath("[].isSummarized").type(JsonFieldType.BOOLEAN).description("요약 완료 여부").attributes(field("constraint", "True: 요약 완료, False: 요약 미완료")),
                                fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("채팅방 업데이트 시간").attributes(field("constraint", "날짜와 시간, 예: 2024-09-03T14:30:00"))
                        )
                ))
                .andReturn();

        final List<ChatRoomResponse> chatRoomResponses = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<ChatRoomResponse>>() {}
        );
    }

    @DisplayName("새로운 채팅방을 만들 수 있다.(V1)")
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

    @DisplayName("오로라AI에게 메시지를 보낼 수 있다.(V1)")
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

    @DisplayName("새로운 채팅방을 만들 수 있다.(V2)")
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

    @DisplayName("오로라AI에게 메시지를 보낼 수 있다.(V2)")
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

    @DisplayName("채팅 내역을 요약할 수 있다. (V1)")
    @Test
    void createNoteV1() throws Exception{
        // given
        when(chatService.createNoteV1(anyLong())).thenReturn(DUMMY_SUCCESS_RESPONSE);

        // when
        final ResultActions resultActions = performCreateNotePostRequestV1();

        // then
        resultActions.andExpect(status().isOk())
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
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요약 성공 여부").attributes(field("constraint", "true: 요약 성공, false: 요약 실패")),
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER).description("노트 ID").attributes(field("constraint", "양의 정수(요약 실패 시 0 반환)"))
                        )
                ));
    }

    @DisplayName("채팅 내역을 요약할 수 있다. (V2)")
    @Test
    void createNoteV2() throws Exception{
        // given
        when(chatService.createNoteV2(anyLong())).thenReturn(DUMMY_SUCCESS_RESPONSE);

        // when
        final ResultActions resultActions = performCreateNotePostRequestV2();

        // then
        resultActions.andExpect(status().isOk())
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
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요약 성공 여부").attributes(field("constraint", "true: 요약 성공, false: 요약 실패")),
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER).description("노트 ID").attributes(field("constraint", "양의 정수(요약 실패 시 0 반환)"))
                        )
                ));
    }


    @DisplayName("채팅방을 삭제할 수 있다.")
    @Test
    void deleteChatRoom() throws Exception{
        // given
        doNothing().when(chatService).validateChatRoomByMember(anyLong(), anyLong());

        // when
        final ResultActions resultActions = performDeleteRequest();

        // then
        verify(chatService).deleteChatRoom(anyLong());

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 ID")
                        )
                ));
    }

    @DisplayName("채팅 내역을 조회할 수 있다.")
    @Test
    void getChatHistory() throws Exception{
        // given
        when(chatService.getChatHistory(anyLong()))
                .thenReturn(List.of(DUMMY_CHAT_DETAIL_RESPONSE_MEMBER, DUMMY_CHAT_DETAIL_RESPONSE_AI));

        // when
        final ResultActions resultActions = performGetChatHistoryResponse();

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
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
                                fieldWithPath("[].contents").type(JsonFieldType.STRING).description("채팅 내용").attributes(field("constraint", "문자열")),
                                fieldWithPath("[].senderType").type(JsonFieldType.STRING).description("채팅 발신자").attributes(key("constraint").value("채팅 발신자를 나타내는 enum 값")),
                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("채팅 생성 시간").attributes(field("constraint", "날짜와 시간, 예: 2024-09-03T14:30:00"))
                        )
                ))
                .andReturn();
    }

    @DisplayName("요약 노트를 채팅방 아이디로 상세 조회할 수 있다.")
    @Test
    void getNote() throws Exception {
        // given
        doNothing().when(chatService).validateSummarizedChatRoom(anyLong(), anyLong());
        when(chatService.getNote(anyLong()))
                .thenReturn(DUMMY_DETAIL_NOTE_RESPONSE);

        // when
        final ResultActions resultActions = performGetNoteDetailRequest();

        // then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
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
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER).description("노트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("target").type(JsonFieldType.STRING).description("대상").attributes(field("constraint", "문자열")),
                                fieldWithPath("problem").type(JsonFieldType.STRING).description("사회 문제").attributes(field("constraint", "문자열")),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("프로젝트 제목").attributes(field("constraint", "문자열")),
                                fieldWithPath("openTitleList").type(JsonFieldType.ARRAY).description("제목 리스트").attributes(key("constraint").value("3개의 문자열(최대 180자)")),
                                fieldWithPath("openSummaryList").type(JsonFieldType.ARRAY).description("요약문 리스트").attributes(key("constraint").value("3개의 문자열(최대 300자)")),
                                fieldWithPath("solution").type(JsonFieldType.STRING).description("해결책").attributes(field("constraint", "문자열"))
                        )
                ))
                .andReturn();

        final DetailNoteResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                DetailNoteResponse.class
        );

        assertThat(response).usingRecursiveComparison().isEqualTo(DUMMY_DETAIL_NOTE_RESPONSE);
    }

}
