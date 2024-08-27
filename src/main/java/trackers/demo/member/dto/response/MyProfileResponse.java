package trackers.demo.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import trackers.demo.member.domain.Member;

@Getter
@AllArgsConstructor
public class MyProfileResponse {

    private final String nickname;

    private final String profileImage;

    private final String email;

    private final String introduction;

    public static MyProfileResponse from(final Member member){
        return new MyProfileResponse(
                member.getNickname(),
                member.getProfileImage(),
                member.getEmail(),
                member.getIntroduction()
        );
    }

}
