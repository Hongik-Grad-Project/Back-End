package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static trackers.demo.chat.domain.type.ChatMessageRole.*;

@Getter
@NoArgsConstructor
public class CompletionRequest {

    private static final String AURORA_AI_ROLE = "너는 친절한 상담가야 답변은 짧고 질문 위주로 해줘";

    private String model;
    private List<CompletionMessage> messages;

    public CompletionRequest(final String model, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new CompletionMessage(SYSTEM.value(), AURORA_AI_ROLE));
        this.messages.add(new CompletionMessage(USER.value(), prompt));
    }

    public CompletionRequest(final String model, final List<CompletionMessage> messages, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new CompletionMessage(SYSTEM.value(), AURORA_AI_ROLE));
        this.messages.addAll(messages);
        this.messages.add(new CompletionMessage(USER.value(), prompt));
    }
}
