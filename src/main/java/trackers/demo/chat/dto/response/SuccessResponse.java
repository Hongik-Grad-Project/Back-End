package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {

    private boolean isSuccess;
    private long noteId;

    public static SuccessResponse of(final boolean isSuccess, final long noteId) {
        return new SuccessResponse(isSuccess, noteId);
    }
}
