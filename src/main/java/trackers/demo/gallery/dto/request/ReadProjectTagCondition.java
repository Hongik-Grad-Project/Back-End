package trackers.demo.gallery.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReadProjectTagCondition {

    @Size(message = "프로젝트 태그를 선택하세요")
    private final List<String> tags;
}
