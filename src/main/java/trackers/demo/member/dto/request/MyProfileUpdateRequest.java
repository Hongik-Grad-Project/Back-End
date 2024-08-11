package trackers.demo.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyProfileUpdateRequest {

    @NotBlank(message = "닉네임은 공백이 될 수 없습니다.")
    @Size(max = 15, message = "닉네임은 15자를 초과할 수 없습니다.")
    private final String nickname;

    @NotBlank(message = "한 줄 소개는 공백이 될 수 없습니다.")
    @Size(max = 50, message = "한 줄 소개는 50자를 초과할 수 없습니다.")
    private final String introduction;
}
