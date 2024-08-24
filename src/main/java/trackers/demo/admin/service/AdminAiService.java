package trackers.demo.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.admin.domain.Assistant;
import trackers.demo.admin.domain.repository.AssistantRepository;
import trackers.demo.admin.dto.request.AssistantCreateRequest;
import trackers.demo.admin.dto.response.AssistantResponse;
import trackers.demo.global.config.ChatGPTConfig;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminAiService {

    private final ChatGPTConfig config;

    private final AssistantRepository assistantRepository;

    // Assistant 생성 로직
    public Long createAssistant(final AssistantCreateRequest assistantCreateRequest) {

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", assistantCreateRequest.getName());
        requestBody.put("instructions", assistantCreateRequest.getInstructions());
        requestBody.put("model", assistantCreateRequest.getModel());

        log.info("RestTemplate으로 ChatGPT API에 POST 요청");
        AssistantResponse response = config.assistantTemplate().postForObject(
                config.getAssistantApiUrl(),
                requestBody,
                AssistantResponse.class
        );

        log.info("Response = {}", response);
        Assistant newAssistant = new Assistant(
                response.getName(),
                response.getId(),
                response.getModel()
        );

        return assistantRepository.save(newAssistant).getId();
    }
}
