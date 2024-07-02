package trackers.demo.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectCreateSecondRequest {

    @NotNull(message = "소제목들을 입력해주세요.(1개 이상)")
    private final List<String> subtitleList;

    @NotNull(message = "본문들을 입력해주세요.(1개 이상)")
    private final List<String> contentList;
}
