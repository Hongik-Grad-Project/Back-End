package trackers.demo.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.project.domain.Target;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ProjectCreateFirstRequest {

    @NotNull(message = "프로젝트의 대상을 선택해주세요.")
    private final String target;

    @NotNull(message = "프로젝트의 주제를 선택해주세요.")
    private final String subject;

    @NotNull(message = "프로젝트 팀원 모집 여부를 선택해주세요.")
    private final Boolean isRecruit;

    private String wantedMember;

    @NotNull(message = "프로젝트 시작 날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startDate;

    @NotNull(message = "프로젝트 종료 날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate endDate;

    @NotNull(message = "프로젝트명을 입력해주세요.")
    private final String projectTitle;


}
