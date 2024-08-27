package trackers.demo.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMessageRequest {

    @NotNull(message = "채팅 내용을 입력해주세요.")
    private String message;

    public CreateMessageRequest(final String message){ this.message = message; }
}
