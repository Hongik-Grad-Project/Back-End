package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.domain.type.ChatMessageRole;

import java.util.ArrayList;
import java.util.List;

import static trackers.demo.chat.domain.type.ChatMessageRole.*;

@Getter
@NoArgsConstructor
public class ChatGPTRequest {

    private static final String AURORA_AI_ROLE = "너는 친절한 상담가야 답변은 짧고 질문 위주로 해줘";

    private String model;
    private List<ChatGPTMessage> messages;

    public ChatGPTRequest(final String model, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new ChatGPTMessage(SYSTEM.value(), AURORA_AI_ROLE));
        this.messages.add(new ChatGPTMessage(USER.value(), prompt));
    }

    public ChatGPTRequest(final String model, final List<ChatGPTMessage> messages, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new ChatGPTMessage(SYSTEM.value(), AURORA_AI_ROLE));
        this.messages.addAll(messages);
        this.messages.add(new ChatGPTMessage(USER.value(), prompt));
    }
}
