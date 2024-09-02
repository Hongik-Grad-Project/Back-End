package trackers.demo.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {

    private String target;
    private String problem;
    private String title;
    private List<String> openTitleList;
    private List<String> openSummaryList;
    private String solution;

}
