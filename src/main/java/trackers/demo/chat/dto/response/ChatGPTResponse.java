package trackers.demo.chat.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import trackers.demo.chat.dto.request.ChatGPTMessage;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChatGPTResponse {

    private  List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice{

        private int index;
        private ChatGPTMessage message;
    }
}
