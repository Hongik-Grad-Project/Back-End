package trackers.demo.note.presentation;

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
import trackers.demo.global.ControllerTest;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.note.dto.response.DetailNoteResponse;
import trackers.demo.note.dto.response.SimpleNoteResponse;
import trackers.demo.note.service.NoteService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;
import static trackers.demo.note.presentation.fixture.NoteFixture.*;

@WebMvcTest(NoteController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class NoteControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;

    @BeforeEach
    void setUp(){
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetNotesRequest() throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/note")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
    }

    private ResultActions performGetNoteDetailRequest() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/note/{noteId}", 1)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON));
    }

    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.delete("/note/{noteId}", 1)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
    }

    @DisplayName("요약 노트 목록을 조회할 수 있다.")
    @Test
    void getNotes() throws Exception {
        // given
        when(noteService.getNotes(anyLong()))
                .thenReturn(List.of(DUMMY_NOTE_RESPONSE_1, DUMMY_NOTE_RESPONSE_2));

        // when
        final ResultActions resultActions = performGetNotesRequest();

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
                                fieldWithPath("[].noteId").type(JsonFieldType.NUMBER).description("채팅방 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("채팅방 이름").attributes(field("constraint", "문자열")),
                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("채팅방 업데이트 시간").attributes(field("constraint", "날짜와 시간, 예: 2024-09-03T14:30:00"))
                        )
                ))
                .andReturn();

        final List<SimpleNoteResponse> simpleNoteResponses = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<SimpleNoteResponse>>() {});

        assertThat(simpleNoteResponses).usingRecursiveComparison()
                .isEqualTo(List.of(DUMMY_NOTE_RESPONSE_1, DUMMY_NOTE_RESPONSE_2));
    }

    @DisplayName("요약 노트를 상세 조회할 수 있다.")
    @Test
    void getNote() throws Exception {
        // given
        doNothing().when(noteService).validateNoteByMemberId(anyLong(), anyLong());
        when(noteService.getNote(anyLong()))
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
                                parameterWithName("noteId").description("노트 ID")
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

    @DisplayName("요약 노트를 삭제할 수 있다.")
    @Test
    void deleteNote() throws Exception {
        // given
        doNothing().when(noteService).validateNoteByMemberId(anyLong(), anyLong());

        // when
        final ResultActions resultActions = performDeleteRequest();

        // then
        verify(noteService).delete(anyLong());

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        pathParameters(
                                parameterWithName("noteId").description("노트 ID")
                        )
                ));
    }


}
