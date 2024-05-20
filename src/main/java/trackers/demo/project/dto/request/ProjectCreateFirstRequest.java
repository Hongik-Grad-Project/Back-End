package trackers.demo.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import trackers.demo.project.domain.Target;

@Getter
@AllArgsConstructor
public class ProjectCreateFirstRequest {

    @NotNull(message = "프로젝트의 대상을 선택해주세요.")
    private final String target;

    @NotNull(message = "프로젝트의 주제를 선택해주세요.")
    private final String subject;

    @NotNull(message = "모금함 제목을 입력해주세요.")
    private final String donationName;

    @NotNull(message = "대표 사진을 등록해주세요.")
    private final String mainImage;
}
