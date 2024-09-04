package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.domain.ChatRoom;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;
    private String chatRoomName;
    private Boolean isSummarized;
    private LocalDateTime updatedAt;

    public static ChatRoomResponse of(final ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getChatRoomName(),
                chatRoom.isSummarized(),
                chatRoom.getUpdatedAt());
    }
}
