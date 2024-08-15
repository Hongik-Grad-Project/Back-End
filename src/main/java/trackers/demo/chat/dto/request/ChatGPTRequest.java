package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChatGPTRequest {

    private String model;
    private List<ChatGPTMessage> messages;

    public ChatGPTRequest(final String model, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new ChatGPTMessage("user", prompt));
    }
}
