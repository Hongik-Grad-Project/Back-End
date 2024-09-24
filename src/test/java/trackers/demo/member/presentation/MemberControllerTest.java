package trackers.demo.member.presentation;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.global.ControllerTest;
import trackers.demo.like.service.LikeService;
import trackers.demo.like.service.LikeSyncScheduler;
import trackers.demo.loginv2.domain.MemberTokens;
import trackers.demo.member.dto.request.MyProfileUpdateRequest;
import trackers.demo.member.dto.response.LikeProjectResponse;
import trackers.demo.member.dto.response.MyPageResponse;
import trackers.demo.member.service.MemberService;
import trackers.demo.project.service.ImageService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trackers.demo.global.restdocs.RestDocsConfiguration.field;
import static trackers.demo.member.fixture.MemberFixture.*;
import static trackers.demo.project.fixture.ProjectFixture.*;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MemberControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");

    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private LikeSyncScheduler likeSyncScheduler;

    @BeforeEach
    void setUp(){
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetMyPageRequest() throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/mypage")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPostRequest(
            final MockMultipartFile newProfileImage,
            final MockMultipartFile updateRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/mypage/update")
                        .file(updateRequest)
                        .file(newProfileImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performGetMyProjectsRequest() throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/mypage/project")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetlikeProjectsRequest() throws Exception{
        return mockMvc.perform(RestDocumentationRequestBuilders.get("/mypage/like")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("마이페이지를 조회할 수 있다.")
    @Test
    void getMyPage() throws Exception{
        // given
        doNothing().when(memberService).validateProfileByMember(anyLong());
        when(memberService.getMyPageInfo(1L))
                .thenReturn(MyPageResponse.of(
                        DUMMY_MEMBER,
                        MY_PROJECTS,
                        LIKE_PROJECTS
                        ));

        // when
        final ResultActions resultActions = performGetMyPageRequest();

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
                                fieldWithPath("myProfile").type(JsonFieldType.OBJECT).description("내 프로필").optional(),
                                fieldWithPath("myProfile.nickname").type(JsonFieldType.STRING).description("사용자 닉네임").attributes(field("constraint", "문자열")).optional(),
                                fieldWithPath("myProfile.profileImage").type(JsonFieldType.STRING).description("사용자 프로필 이미지").attributes(field("constraint", "문자열")).optional(),
                                fieldWithPath("myProfile.email").type(JsonFieldType.STRING).description("사용자 이메일").attributes(field("constraint", "문자열")).optional(),
                                fieldWithPath("myProfile.introduction").type(JsonFieldType.STRING).description("사용자 한 줄 소개").attributes(field("constraint", "문자열")).optional(),
                                fieldWithPath("myProjects").type(JsonFieldType.ARRAY).description("내 프로젝트 배열").attributes(field("constraint", "문자열")).optional(),
                                fieldWithPath("myProjects[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("myProjects[].mainImagePath").type(JsonFieldType.STRING).description("프로젝트 대표 이미지").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("myProjects[].projectTitle").type(JsonFieldType.STRING).description("프로젝트 제목").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("myProjects[].summary").type(JsonFieldType.STRING).description("사회문제 요약").attributes(key("constraint").value("문자열")),
                                fieldWithPath("myProjects[].target").type(JsonFieldType.STRING).description("프로젝트 대상").attributes(key("constraint").value("문자열")),
                                fieldWithPath("myProjects[].endDate").type(JsonFieldType.STRING).description("프로젝트 종료 날짜").attributes(key("constraint").value("yyyy-MM-dd")),
                                fieldWithPath("myProjects[].completedStatusType").type(JsonFieldType.STRING).description("프로젝트 작성 완료 여부").attributes(key("constraint").value("완료 상태를 나타내는 enum 값")),
                                fieldWithPath("myProjects[].isLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부").attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제")),
                                fieldWithPath("myProjects[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("likeProjects").type(JsonFieldType.ARRAY).description("응원한 프로젝트 배열").attributes(field("constraint", "문자열")).optional(),
                                fieldWithPath("likeProjects[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("likeProjects[].target").type(JsonFieldType.STRING).description("프로젝트 대상").attributes(key("constraint").value("문자열")),
                                fieldWithPath("likeProjects[].projectTitle").type(JsonFieldType.STRING).description("프로젝트 제목").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("likeProjects[].endDate").type(JsonFieldType.STRING).description("프로젝트 종료 날짜").attributes(key("constraint").value("yyyy-MM-dd"))
                        )
                )).andReturn();

        final MyPageResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                MyPageResponse.class
        );

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(MyPageResponse.of(
                        DUMMY_MEMBER,
                        MY_PROJECTS,
                        LIKE_PROJECTS
                ));
    }

    @DisplayName("내 프로필을 수정할 수 있다.")
    @Test
    void updateMyProfile() throws Exception{
        // given
        doNothing().when(memberService).validateProfileByMember(anyLong());

        final MyProfileUpdateRequest updateProfileRequest = new MyProfileUpdateRequest(
                "테스트 사용자 1234",
                "테스트 한 줄 소개입니다."
        );

        final MockMultipartFile newProfileImage = new MockMultipartFile(
                "file",
                "profileImage.png",
                "multipart/form-data",
                "./src/test/resources/static/images/user-icon.png".getBytes()
        );

        final MockMultipartFile updateRequest = new MockMultipartFile(
                "dto",
                null,
                "application/json",
                objectMapper.writeValueAsString(updateProfileRequest).getBytes(UTF_8)
        );

        // when
        final ResultActions resultActions = performPostRequest(newProfileImage, updateRequest);

        // then
        verify(memberService).updateProfile(anyLong(), any(MyProfileUpdateRequest.class), any());

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("access token").attributes(field("constraint", "문자열(jwt)"))
                        ),
                        requestParts(
                                partWithName("dto").description("내 프로필 수정 객체"),
                                partWithName("file").description("프로필 사진. 지원되는 형식은 .png, .jpg 등이 있습니다.")
                        ),
                        requestPartFields("dto",
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임").attributes(key("constraint").value("문자열")),
                                fieldWithPath("introduction").type(JsonFieldType.STRING).description("한 줄 소개").attributes(key("constraint").value("문자열"))
                        ))
                );
    }

    @DisplayName("나의 프로젝트를 모두 조회할 수 있다.")
    @Test
    void getMyProjects() throws Exception{
        // given
        doNothing().when(memberService).validateProfileByMember(anyLong());
        when(memberService.getMyProjects(anyLong())).thenReturn(MY_PROJECTS);

        // when
        final ResultActions resultActions = performGetMyProjectsRequest();

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
                                fieldWithPath("[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].mainImagePath").type(JsonFieldType.STRING).description("프로젝트 대표 이미지").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].projectTitle").type(JsonFieldType.STRING).description("프로젝트 제목").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].summary").type(JsonFieldType.STRING).description("사회문제 요약").attributes(key("constraint").value("문자열")),
                                fieldWithPath("[].target").type(JsonFieldType.STRING).description("프로젝트 대상").attributes(key("constraint").value("문자열")),
                                fieldWithPath("[].endDate").type(JsonFieldType.STRING).description("프로젝트 종료 날짜").attributes(key("constraint").value("yyyy-MM-dd")),
                                fieldWithPath("[].completedStatusType").type(JsonFieldType.STRING).description("프로젝트 작성 완료 여부").attributes(key("constraint").value("완료 상태를 나타내는 enum 값")),
                                fieldWithPath("[].isLike").type(JsonFieldType.BOOLEAN).description("좋아요 여부").attributes(field("constraint", "True: 좋아요 반영, False: 좋아요 해제")),
                                fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수").attributes(field("constraint", "양의 정수"))
                        )
                )).andReturn();

        final List<ProjectResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<ProjectResponse>>() {}
        );

        assertThat(response).usingRecursiveComparison().isEqualTo(MY_PROJECTS);
    }

    @DisplayName("좋아요 한 프로젝트를 모두 조회할 수 있다.")
    @Test
    void getLikeProjects() throws Exception{
        // given
        doNothing().when(memberService).validateProfileByMember(anyLong());
        when(memberService.getLikeProjects(anyLong())).thenReturn(LIKE_PROJECTS);

        // when
        final ResultActions resultActions = performGetlikeProjectsRequest();

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
                                fieldWithPath("[].projectId").type(JsonFieldType.NUMBER).description("프로젝트 ID").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].target").type(JsonFieldType.STRING).description("프로젝트 대상").attributes(key("constraint").value("문자열")),
                                fieldWithPath("[].projectTitle").type(JsonFieldType.STRING).description("프로젝트 제목").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("[].endDate").type(JsonFieldType.STRING).description("프로젝트 종료 날짜").attributes(key("constraint").value("yyyy-MM-dd"))
                        )
                )).andReturn();

        final List<LikeProjectResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LikeProjectResponse>>() {}
        );

        assertThat(response).usingRecursiveComparison().isEqualTo(LIKE_PROJECTS);
    }

}
