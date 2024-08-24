package trackers.demo.admin.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.admin.dto.request.AssistantCreateRequest;
import trackers.demo.admin.service.AdminAiService;
import trackers.demo.auth.AdminAuth;
import trackers.demo.auth.AdminOnly;
import trackers.demo.auth.domain.Accessor;

import java.net.URI;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAiController {

    private final AdminAiService adminAiService;


//    @AdminOnly
    @PostMapping("/assistant")
    public ResponseEntity<Void> createAssistant(
//            @AdminAuth final Accessor accessor,
            @RequestBody @Valid AssistantCreateRequest assistantCreateRequest
    ){
//        log.info("adminId = {}의 Assistant 생성 요청이 들어왔습니다,", accessor.getMemberId());
        final Long assistantId = adminAiService.createAssistant(assistantCreateRequest);
        return ResponseEntity.created(URI.create("/admin/assistant/" + assistantId)).build();
    }
}
