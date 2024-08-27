package trackers.demo.chat.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThreadResponse {

    private String id;
    private String status;
    private String createdAt;

}
