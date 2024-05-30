package trackers.demo.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.project.domain.Target;

@Getter
@AllArgsConstructor
public class ProjectCreateFirstRequest {

    @NotNull(message = "프로젝트의 대상을 선택해주세요.")
    private final String target;

    @NotNull(message = "프로젝트의 주제를 선택해주세요.")
    private final String subject;

    @NotNull(message = "프로젝트명을 입력해주세요.")
    private final String projectTitle;

}
