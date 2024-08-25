package trackers.demo.chat.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.dto.request.CompletionMessage;

import java.util.List;

@Getter
@NoArgsConstructor
public class CompletionResponse {

    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice{

        private int index;
        private CompletionMessage message;
    }
}

