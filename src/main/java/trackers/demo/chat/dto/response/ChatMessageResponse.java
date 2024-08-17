package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private String responseMessage;

    public static ChatMessageResponse of(final String receivedMessage) {
        return new ChatMessageResponse(receivedMessage);
    }
}
