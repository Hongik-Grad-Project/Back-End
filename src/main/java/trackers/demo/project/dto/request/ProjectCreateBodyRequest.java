package trackers.demo.project.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectCreateBodyRequest {

    @NotNull(message = "소제목들을 입력해주세요.")
    @Size(max = 3, message = "소제목은 최대 3개 입니다")
    private final List<String> subtitleList;

    @NotNull(message = "본문들을 입력해주세요.(1개 이상)")
    @Size(max = 3, message = "본문은 최대 3개 입니다")
    private final List<String> contentList;

    @Size(max = 10, message = "태그의 수는 최대 10개 입니다")
    private final List<String> tagList;
}
