package trackers.demo.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ProjectUpdateOutlineRequest {

    @NotNull(message = "프로젝트의 대상을 선택해주세요.")
    private final String target;

    @NotNull(message = "사회 문제 요약을 입력해주세요")
    private final String summary;

    @NotNull(message = "프로젝트 시작 날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startDate;

    @NotNull(message = "프로젝트 종료 날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate endDate;

    @NotNull(message = "프로젝트명을 입력해주세요.")
    private final String projectTitle;
}
