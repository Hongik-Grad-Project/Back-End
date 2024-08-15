package trackers.demo.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.auth.Auth;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.chat.service.ChatService;

import java.net.URI;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Void> createChatRoom(@Auth final Accessor accessor){
        log.info("memberId={}의 새로운 채팅방 생성 요청이 들어왔습니다.", accessor.getMemberId());
        final Long chatRoomId = chatService.create(accessor.getMemberId());
        return ResponseEntity.created(URI.create("/chat/" + chatRoomId)).build();
    }
}
