package trackers.demo.note.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutomatedProposalResponse {

    private List<String> titleList;
    private List<String> contentList;
}
