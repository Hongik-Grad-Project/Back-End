package trackers.demo.gallery.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReadProjectFilterCondition {

//    @NotNull(message = "모금 여부를 선택해주세요.(default: false)")
//    private final boolean isDonated;

    @NotNull(message = "프로젝트 대상을 선택하세요")
    private final List<String> targets;

}
