package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static trackers.demo.chat.domain.type.ChatMessageRole.*;

@Getter
@NoArgsConstructor
public class CompletionRequest {

    private String model;
    private List<CompletionMessage> messages;

    public CompletionRequest(final String role, final String model, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new CompletionMessage(SYSTEM.value(), role));
        this.messages.add(new CompletionMessage(USER.value(), prompt));
    }

    public CompletionRequest(final String role, final String model, final List<CompletionMessage> messages, final String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new CompletionMessage(SYSTEM.value(),role));
        this.messages.addAll(messages);
        this.messages.add(new CompletionMessage(USER.value(), prompt));
    }
}
