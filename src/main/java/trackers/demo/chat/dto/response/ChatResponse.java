package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String responseMessage;

    public static ChatResponse of(final String receivedMessage) {
        return new ChatResponse(receivedMessage);
    }
}
