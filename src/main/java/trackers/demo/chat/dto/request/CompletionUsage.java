package trackers.demo.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompletionUsage {

    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;

    public CompletionUsage(final int prompt_tokens, final int completion_tokens, final int total_tokens) {
        this.prompt_tokens = prompt_tokens;
        this.completion_tokens = completion_tokens;
        this.total_tokens = total_tokens;
    }
}
