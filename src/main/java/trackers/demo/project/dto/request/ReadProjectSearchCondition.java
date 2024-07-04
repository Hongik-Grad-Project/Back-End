package trackers.demo.project.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadProjectSearchCondition {

    @Size(max = 10, message = "...")
    private final String title;
}
