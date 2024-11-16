package trackers.demo.chat.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.dto.request.CompletionMessage;
import trackers.demo.chat.dto.request.CompletionRequest;
import trackers.demo.chat.dto.request.CompletionUsage;

import java.util.List;

@Getter
@NoArgsConstructor
public class CompletionResponse {

    private List<Choice> choices;
    private CompletionUsage usage;

    @Getter
    @NoArgsConstructor
    public static class Choice{

        private int index;
        private CompletionMessage message;
    }
}

