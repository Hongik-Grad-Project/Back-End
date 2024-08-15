package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatGPTMessage {

    private String role;

    private String content;

    public ChatGPTMessage(final String role, final String content){
        this.role = role;
        this.content = content;
    }
}
