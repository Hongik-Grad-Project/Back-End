package trackers.demo.chat.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageResponse {

    private final String responseMessage;

    public static ChatMessageResponse of(final String receivedMessage) {
        return new ChatMessageResponse(receivedMessage);
    }
}
