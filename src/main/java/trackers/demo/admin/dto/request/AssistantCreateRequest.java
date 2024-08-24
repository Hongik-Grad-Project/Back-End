package trackers.demo.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AssistantCreateRequest {

    @NotNull(message = "채팅봇의 이름을 입력해주세요")
    @Size(max = 20, message = "채팅봇의 이름은 20자를 초과할 수 없습니다.")
    private String name;

    @NotNull(message = "채팅봇에 부여하고 싶은 역할을 지정해주세요")
    private String instructions;    // 추후에 파일 형식으로 받을 수 있도록 리팩토링

    @NotNull(message = "모델명을 입력해주세요")
    private String model;

}
