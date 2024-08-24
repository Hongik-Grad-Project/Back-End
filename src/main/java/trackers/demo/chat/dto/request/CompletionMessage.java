package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompletionMessage {

    private String role;

    private String content;

    public CompletionMessage(final String role, final String content){
        this.role = role;
        this.content = content;
    }
}
