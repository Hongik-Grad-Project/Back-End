package trackers.demo.chat.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.ChatDetailResponse;
import trackers.demo.chat.dto.response.ChatResponse;
import trackers.demo.chat.dto.response.ChatRoomResponse;
import trackers.demo.chat.dto.response.SuccessResponse;
import trackers.demo.chat.service.ChatService;
import trackers.demo.note.dto.response.DetailNoteResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    @MemberOnly
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@Auth final Accessor accessor) {
        log.info("memberId={}의 채팅방 내역 조회 요청이 들어왔습니다.", accessor.getMemberId());
        final List<ChatRoomResponse> chatRoomResponses = chatService.getChatRooms(accessor.getMemberId());
        return ResponseEntity.ok(chatRoomResponses);
    }

    @PostMapping("/v1")
    @MemberOnly
    public ResponseEntity<Void> createChatRoomV1(@Auth final Accessor accessor){
        log.info("memberId={}의 새로운 채팅방 생성 요청이 들어왔습니다. (V1)", accessor.getMemberId());
        final Long chatRoomId = chatService.createRoomV1(accessor.getMemberId());
        return ResponseEntity.created(URI.create("/chat/" + chatRoomId)).build();
    }

    @PostMapping("/{chatRoomId}/message/v1")
    @MemberOnly
    public ResponseEntity<ChatResponse> createMessageV1(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") final Long chatRoomId,
            @RequestBody @Valid final CreateMessageRequest request
    ){
        log.info("memberId={}의 채팅 메시지 생성 요청이 들어왔습니다. (V1)", accessor.getMemberId());
        final ChatResponse chatMessageResponse = chatService.createMessageV1(
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
    public ResponseEntity<ChatResponse> createMessageV2(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") final Long chatRoomId,
            @RequestBody @Valid final CreateMessageRequest request
    ) throws InterruptedException {
        log.info("memberId={}의 chatRoomId={}에 채팅 메시지 생성 요청이 들어왔습니다. (V2)", accessor.getMemberId(), chatRoomId);
        final ChatResponse chatMessageResponse = chatService.createMessageV2(
                accessor.getMemberId(),
                chatRoomId,
                request
        );
        return ResponseEntity.ok().body(chatMessageResponse);
    }

    @PostMapping("/{chatRoomId}/summary/v1")
    @MemberOnly
    public ResponseEntity<SuccessResponse> createNoteV1(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") Long chatRoomId
    ) throws JsonProcessingException {
        log.info("memberId={}의 chatRoomId={} 요약 노트 생성하기 요청이 들어왔습니다. (V1)", accessor.getMemberId(), chatRoomId);
        final SuccessResponse successResponse = chatService.createNoteV1(chatRoomId);
        return ResponseEntity.ok().body(successResponse);
    }

    @PostMapping("/{chatRoomId}/summary/v2")
    @MemberOnly
    public ResponseEntity<SuccessResponse> createNoteV2(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") Long chatRoomId
    ) throws InterruptedException, JsonProcessingException {
        log.info("memberId={}의 chatRoomId={} 요약 노트 생성하기 요청이 들어왔습니다. (V2)", accessor.getMemberId(), chatRoomId);
        final SuccessResponse successResponse = chatService.createNoteV2(chatRoomId);
        return ResponseEntity.ok().body(successResponse);
    }

    @DeleteMapping("/{chatRoomId}")
    @MemberOnly
    public ResponseEntity<Void> deleteChatRoom(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") Long chatRoomId
    ){
        log.info("memberId={}의 chatRoomId={} 삭제 요청이 들어왔습니다.", accessor.getMemberId(), chatRoomId);
        chatService.validateChatRoomByMember(accessor.getMemberId(), chatRoomId);
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatRoomId}/history")
    @MemberOnly
    public ResponseEntity<List<ChatDetailResponse>> getChatHistory(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") final Long chatRoomId
    ){
        log.info("memberId={}의 chatRoomId={} 채팅 내역 조회 요청이 들어왔습니다.", accessor.getMemberId(), chatRoomId);
        final List<ChatDetailResponse> chatDetailResponses = chatService.getChatHistory(chatRoomId);
        return ResponseEntity.ok().body(chatDetailResponses);
    }

    @GetMapping("/{chatRoomId}/note")
    @MemberOnly
    public ResponseEntity<DetailNoteResponse> getNote(
            @Auth final Accessor accessor,
            @PathVariable("chatRoomId") final Long chatRoomId
    ){
        log.info("memberId={}의 chatRoomId={}의 노트 조회 요청이 들어왔습니다.", accessor.getMemberId(), chatRoomId);
        chatService.validateSummarizedChatRoom(accessor.getMemberId(), chatRoomId);
        final DetailNoteResponse detailNoteResponse = chatService.getNote(chatRoomId);
        return ResponseEntity.ok(detailNoteResponse);
    }

}
