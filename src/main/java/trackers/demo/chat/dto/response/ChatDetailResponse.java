package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.type.SenderType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDetailResponse {

    private String contents;

    private SenderType senderType;

    private LocalDateTime createdAt;

    public static ChatDetailResponse of(final Message message) {
        return new ChatDetailResponse(
                message.getContents(),
                message.getSenderType(),
                message.getCreatedAt()
        );
    }
}
