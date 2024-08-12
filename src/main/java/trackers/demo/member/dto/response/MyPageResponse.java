package trackers.demo.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.member.domain.Member;
import trackers.demo.project.domain.Project;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MyPageResponse {

    // 프로필 정보
    private final MyProfileResponse myProfile;

    // 나의 프로젝트
    private final List<ProjectResponse> myProjects;

    // 내가 응원한 프로젝트
    private final List<LikeProjectResponse> likeProjects;

    public static MyPageResponse of(
            final Member member,
            final List<ProjectResponse> myProjectResponses,
            final List<LikeProjectResponse> likeProjectResponses) {

        final MyProfileResponse myProfileResponse = MyProfileResponse.from(member);

        return new MyPageResponse(
                myProfileResponse,
                myProjectResponses,
                likeProjectResponses
        );
    }

}
