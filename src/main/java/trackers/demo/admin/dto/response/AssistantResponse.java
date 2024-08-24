package trackers.demo.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class AssistantResponse {

    private String id;
    private String object;
    private long created;
    private String name;
    private String description;
    private String instructions;
    private String model;
    private String status;
    private List<Tool> tools;
    private ToolResources toolResources;

    @Getter
    @NoArgsConstructor
    public static class Tool {
        private String type;
    }

    @Getter
    @NoArgsConstructor
    public static class ToolResources {
        private Map<String, CodeInterpreter> codeInterpreter;

        @Getter
        @NoArgsConstructor
        public static class CodeInterpreter {
            private List<String> fileIds;
        }
    }
}
