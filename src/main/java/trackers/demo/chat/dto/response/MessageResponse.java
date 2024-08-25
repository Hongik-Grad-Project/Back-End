package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MessageResponse {

    private String id;
    private String role;
//    private List<Content> content;
    private String createdAt;

//    @Getter
//    @NoArgsConstructor
//    public static class Content {
//        private String type;
//        private Text text;
//
//        @Getter
//        @NoArgsConstructor
//        public static class Text {
//            private String value;
//            private List<Object> annotations; // 필요한 경우 적절한 타입으로 수정
//        }
//    }

}
