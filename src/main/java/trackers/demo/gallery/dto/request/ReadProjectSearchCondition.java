package trackers.demo.gallery.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadProjectSearchCondition {

    @NotNull(message = "검색어를 입력하세요.")
    @Size(max = 30, message = "검색어는 30자를 초과할 수 없습니다.")
    private final String keyword;
}
