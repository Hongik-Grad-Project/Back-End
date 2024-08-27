package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RunResponse {

    private String id;
    private String status;
    private String result;
    private String last_error;

}
