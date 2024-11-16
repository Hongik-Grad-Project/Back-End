package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import trackers.demo.chat.dto.request.CompletionUsage;

@Getter
@AllArgsConstructor
public class RunResponse {

    private String id;
    private String status;
    private String result;
    private String last_error;
    private CompletionUsage usage;

}
