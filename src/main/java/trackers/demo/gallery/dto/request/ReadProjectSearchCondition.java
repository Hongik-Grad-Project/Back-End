package trackers.demo.gallery.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadProjectSearchCondition {

    @Size(max = 10, message = "검색어를 입력하세요.")
    private final String title;
}
