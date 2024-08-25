package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ThreadMessageResponse {

    private List<Message> data;

    // Inner class to represent each message
    @Getter
    @NoArgsConstructor
    public static class Message {
        private String id;
        private String role;
        private List<Content> content;

        @Getter
        @NoArgsConstructor
        public static class Content {
            private Text text;

            @Getter
            @NoArgsConstructor
            public static class Text {
                private String value;

            }
        }
    }

}
