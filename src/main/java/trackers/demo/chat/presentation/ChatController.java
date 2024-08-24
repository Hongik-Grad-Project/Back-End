package trackers.demo.chat.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.auth.domain.Authority;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.ChatMessageResponse;
import trackers.demo.chat.service.ChatService;

import java.net.URI;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/v1")
    @MemberOnly
    public ResponseEntity<Void> createChatRoomV1(@Auth final Accessor accessor){
        log.info("memberId={}의 새로운 채팅방 생성 요청이 들어왔습니다. (V1)", accessor.getMemberId());
        final Long chatRoomId = chatService.createRoomV1(accessor.getMemberId());
        return ResponseEntity.created(URI.create("/chat/" + chatRoomId)).build();
    }

    @PostMapping("/{chatRoomId}/message/v1")
    @MemberOnly
    public ResponseEntity<ChatMessageResponse> createMessageV1(
            @Auth final Accessor accessor,
            @PathVariable final Long chatRoomId,
            @RequestBody @Valid final CreateMessageRequest request
    ){
        log.info("memberId={}의 채팅 메시지 생성 요청이 들어왔습니다. (V1)", accessor.getMemberId());
        final ChatMessageResponse chatMessageResponse = chatService.createMessageV1(
                accessor.getMemberId(),
                chatRoomId,
                request
        );
        return ResponseEntity.ok().body(chatMessageResponse);
    }

    @PostMapping("/v2")
    @MemberOnly
    public ResponseEntity<Void> createChatRoomV2(@Auth final Accessor accessor){
//        Accessor accessor = new Accessor(1L, Authority.MEMBER);
        log.info("memberId={}의 채팅방 생성 요청이 들어왔습니다. (V2)", accessor.getMemberId());
        final Long chatRoomId = chatService.createRoomV2(accessor.getMemberId());
        return ResponseEntity.created(URI.create("/chat/" + chatRoomId)).build();
    }


    @PostMapping("/{chatRoomId}/message/v2")
    @MemberOnly
    public ResponseEntity<ChatMessageResponse> createMessageV2(
            @Auth final Accessor accessor,
            @PathVariable final Long chatRoomId,
            @RequestBody @Valid final CreateMessageRequest request
    ){
//        Accessor accessor = new Accessor(1L, Authority.MEMBER);
        log.info("memberId={}의 채팅 메시지 생성 요청이 들어왔습니다. (V2)", accessor.getMemberId());
        final ChatMessageResponse chatMessageResponse = chatService.createMessageV2(
                accessor.getMemberId(),
                chatRoomId,
                request
        );
        return ResponseEntity.ok().body(chatMessageResponse);
    }

    // todo: 채팅방 대화 목록 조회 API ( GET: {chatRoomId}/history )
}
