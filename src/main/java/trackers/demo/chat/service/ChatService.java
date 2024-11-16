package trackers.demo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import trackers.demo.admin.domain.Assistant;
import trackers.demo.admin.domain.repository.AssistantRepository;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.chat.domain.repository.MessageRepository;
import trackers.demo.chat.dto.request.CompletionMessage;
import trackers.demo.chat.dto.request.CompletionRequest;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.*;
import trackers.demo.global.config.ChatGPTConfig;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.note.domain.Note;
import trackers.demo.note.domain.repository.NoteRepository;
import trackers.demo.note.dto.response.DetailNoteResponse;

import java.util.*;
import java.util.stream.Collectors;

import static trackers.demo.chat.domain.type.ChatMessageRole.*;
import static trackers.demo.chat.domain.type.SenderType.*;
import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private static final String AURORA_AI_CHAT_BOT = "오로라 AI 챗봇";
    private static final String AURORA_AI_NOTE_BOT = "오로라 AI 요약";

    private static final String SUMMARIZE_MESSAGE = "대화 내용을 바탕으로 요약해줘";
    private static final String DEFAULT_CHAT_ROOM_NAME = "새로운 채팅";
    private static final String DEFAULT_THREAD_ID = "dummy_thread_id";

    private final ChatGPTConfig config;

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final AssistantRepository assistantRepository;
    private final NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRooms(final Long memberId) {
        final List<ChatRoom> chatRooms = chatRoomRepository.findByMemberId(memberId);

        return chatRooms.stream()
                .map(ChatRoomResponse::of)
                .collect(Collectors.toList());
    }

    public Long createRoomV1(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new  BadRequestException(NOT_FOUND_MEMBER));

        final ChatRoom chatRoom = new ChatRoom(member, DEFAULT_CHAT_ROOM_NAME, DEFAULT_THREAD_ID);
        return chatRoomRepository.save(chatRoom).getId();
    }

    public ChatResponse createMessageV1(
            final Long memberId,
            final Long chatRoomId,
            final CreateMessageRequest request) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_CHAT_ROOM));

        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        final Message sentMessage = new Message(chatRoom, request.getMessage(), MEMBER);
        messageRepository.save(sentMessage);

        final String receivedMessage = getResponse(request.getMessage(), chatRoomId);
        messageRepository.save(new Message(chatRoom, receivedMessage, AURORA_AI));

        return ChatResponse.of(receivedMessage);
    }

    private String getResponse(final String prompt, final Long chatRoomId) {
        log.info("프롬프트 수행");
        CompletionRequest request;
        if(!messageRepository.existsByChatRoomId(chatRoomId)){      // 처음 텍스트 생성 (이전 채팅 내역 존재X)
            request = new CompletionRequest(config.getModel(), prompt);
        } else {    // 이전 채팅 내역 추가
            final List<CompletionMessage> chatGPTMessages = new ArrayList<>();
            final List<Message> messages = messageRepository.findAllByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
            for(final Message message : messages){
                CompletionMessage historyMessage = null;
                if(message.getSenderType().equals(AURORA_AI)){
                    historyMessage = new CompletionMessage(ASSISTANT.value(), message.getContents());
                } else if (message.getSenderType().equals(MEMBER)) {
                    historyMessage = new CompletionMessage(USER.value(), message.getContents());
                }
                chatGPTMessages.add(historyMessage);
            }
            request = new CompletionRequest(config.getModel(), chatGPTMessages, prompt);
        }

        log.info("RestTemplate으로 ChatGPT API에 POST 요청");
        CompletionResponse completionResponse = config.completionTemplate().postForObject(
                config.getCompletionApiUrl(),
                request,
                CompletionResponse.class
        );

        log.info("응답 받기 성공");
        String response = completionResponse.getChoices().get(0).getMessage().getContent();

//        // 토큰 사용량 분석용 코드
//        int prompt_tokens = completionResponse.getUsage().getPrompt_tokens();
//        log.info("prompt_tokens = {}", prompt_tokens);
//        int completion_tokens = completionResponse.getUsage().getCompletion_tokens();
//        log.info("completion_tokens = {}", completion_tokens);
//        int total_tokens = completionResponse.getUsage().getTotal_tokens();
//        log.info("total_tokens = {}", total_tokens);

        return response;
    }

    public Long createRoomV2(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new  BadRequestException(NOT_FOUND_MEMBER));

        Map<String, Object> requestBody = new HashMap<>();
        // requestBody에 초기 메시지 추가 가능

        log.info("RestTemplate으로 ChatGPT API에 POST 요청");
        ThreadResponse response = config.assistantTemplate().postForObject(
                config.getThreadApiUrl(),
                requestBody,
                ThreadResponse.class
        );

        log.info("Response = {}", response);
        ChatRoom newChatRoom = new ChatRoom(member, DEFAULT_CHAT_ROOM_NAME, response.getId());

        return chatRoomRepository.save(newChatRoom).getId();
    }

    public ChatResponse createMessageV2(
            final Long memberId,
            final Long chatRoomId,
            final CreateMessageRequest request) throws InterruptedException {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_CHAT_ROOM));

        final Assistant assistant = assistantRepository.findByName(AURORA_AI_CHAT_BOT)
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_ASSISTANT));

        final Message sentMessage = new Message(chatRoom, request.getMessage(), MEMBER);
        messageRepository.save(sentMessage);

        log.info("1. 사용자 메시지 생성하기");
        final MessageResponse messageResponse = sendMessage(chatRoom.getThread(), request.getMessage());

        log.info("2. Run 객체 실행");
        final RunResponse initialRunResponse = createRun(assistant.getAssistantId(), chatRoom.getThread());

        log.info("3. Run 객체 갱신");
        final RunResponse completedRunResponse = updateRun(initialRunResponse.getId(), chatRoom.getThread());

        log.info("4. 응답 메시지 추출");
        final ThreadMessageResponse.Message lastAssistantMessage = getThreadMessage(chatRoom.getThread());
        final String receivedMessage = lastAssistantMessage.getContent().get(0).getText().getValue();

        messageRepository.save(new Message(chatRoom, receivedMessage, AURORA_AI));
        return ChatResponse.of(receivedMessage);
    }

    public SuccessResponse createNote(final Long chatRoomId) throws InterruptedException, JsonProcessingException {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_CHAT_ROOM));

        final Assistant assistant = assistantRepository.findByName(AURORA_AI_NOTE_BOT)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ASSISTANT));

        log.info("1. 사용자 메시지 생성하기");
        final MessageResponse messageResponse = sendMessage(chatRoom.getThread(), SUMMARIZE_MESSAGE);

        log.info("2. Run 객체 실행");
        final RunResponse initialRunResponse = createRun(assistant.getAssistantId(), chatRoom.getThread());

        log.info("3. Run 객체 갱신");
        final RunResponse completedRunResponse = updateRun(initialRunResponse.getId(), chatRoom.getThread());

        log.info("4. 응답 메시지 추출");
        final ThreadMessageResponse.Message lastAssistantMessage = getThreadMessage(chatRoom.getThread());
        final String receivedMessage = lastAssistantMessage.getContent().get(0).getText().getValue();

        log.info("5. Thread에서 마지막 메시지 두개 지우기");
        deleteMessageInThread(chatRoom.getThread(), messageResponse.getId(), lastAssistantMessage.getId());

        log.info("6. 응답 추출 및 저장");
        return createNewNote(receivedMessage, chatRoom);
    }

    private MessageResponse sendMessage(final String threadId, final String content) {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getMessageApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("role", "user");
        requestBody.put("content", content);

        final MessageResponse response = config.assistantTemplate().postForObject(
                url,
                requestBody,
                MessageResponse.class
        );
        return response;
    }

    private RunResponse createRun(final String assistantId, final String threadId) {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getRunApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("assistant_id", assistantId);

        final RunResponse response = config.assistantTemplate().postForObject(
                url,
                requestBody,
                RunResponse.class
        );
        return response;
    }

    private RunResponse updateRun(final String runId, final String threadId) throws InterruptedException {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getRunApiUrl() + "/" + runId)
                .buildAndExpand(threadId)
                .toUriString();

        final long startTime = System.currentTimeMillis();

        while (true) {
            final RunResponse retrievedRun = config.assistantTemplate().getForObject(url, RunResponse.class);

            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            log.info("Run status: {}, 경과: {}초", retrievedRun.getStatus(), (float) elapsedTime);

            // 상태가 completed일 경우 종료
            if ("completed".equals(retrievedRun.getStatus())) {
//                // 토큰 사용량 분석용 코드
//                int prompt_tokens = retrievedRun.getUsage().getPrompt_tokens();
//                log.info("prompt_tokens = {}", prompt_tokens);
//                int completion_tokens = retrievedRun.getUsage().getCompletion_tokens();
//                log.info("completion_tokens = {}", completion_tokens);
//                int total_tokens = retrievedRun.getUsage().getTotal_tokens();
//                log.info("total_tokens = {}", total_tokens);
//                return retrievedRun;
            }
            // 상태가 [실패, 취소, 만료]된 경우 예외 발생
            if (retrievedRun.getStatus().equals("failed") ||
                    retrievedRun.getStatus().equals("cancelled") ||
                    retrievedRun.getStatus().equals("expired")) {
                throw new RuntimeException("Run failed: " + retrievedRun.getLast_error());
            }
            // 1초 대기
            Thread.sleep(1000);
        }
    }

    private ThreadMessageResponse.Message getThreadMessage(final String threadId) {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getThreadMessageApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        final ThreadMessageResponse response = config.assistantTemplate().getForObject(
                url,
                ThreadMessageResponse.class
        );

        final List<ThreadMessageResponse.Message> messages = response.getData();

        final List<ThreadMessageResponse.Message> assistantMessages = messages.stream()
                .filter(message -> "assistant".equals(message.getRole()))  // AI의 메시지만 선택
                .collect(Collectors.toList());

        log.info("assistantMessages.size() = {}", assistantMessages.size());
        log.info("messages.size() = {}", messages.size());

        // 첫 번째 메시지의 내용을 반환
        if (!assistantMessages.isEmpty()) {
            return assistantMessages.get(0);
//            return lastAssistantMessage.getContent().get(0).getText().getValue();
        } else {
            throw new BadRequestException(NOT_FOUND_MESSAGE_IN_THREAD);
        }
    }

    private SuccessResponse createNewNote(final String receivedMessage, final ChatRoom chatRoom) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        final String trimmedMessage = receivedMessage.replaceAll(".*(\\{.*\\}).*", "$1");

        log.info("정제 전 요약 응답: {}", receivedMessage);
        log.info("정제 후 요약 응답: {}", trimmedMessage);

        final NoteResponse noteResponse = objectMapper.readValue(trimmedMessage, NoteResponse.class);

        if(noteResponse == null || noteResponse.getTarget().equals("null")) {
            return SuccessResponse.of(false, 0);
        }

        final Note note = Note.of(
                noteResponse.getTarget(),
                noteResponse.getProblem(),
                noteResponse.getTitle(),
                noteResponse.getOpenTitleList(),
                noteResponse.getOpenSummaryList(),
                noteResponse.getSolution(),
                chatRoom
        );

        chatRoom.updateChatRoomName(noteResponse.getTitle());
        chatRoom.updateIsSummarized(true);
        chatRoomRepository.save(chatRoom);

        return SuccessResponse.of(true, noteRepository.save(note).getId());
    }

    private void deleteMessageInThread(final String threadId, final String memberMessageId, final String aiMessageId) {
        String memberMessageUrl = UriComponentsBuilder.fromHttpUrl(config.getDeleteMessageApiUrl())
                .buildAndExpand(threadId, memberMessageId)
                .toUriString();
        config.assistantTemplate().delete(memberMessageUrl);

        String aiMessageUrl = UriComponentsBuilder.fromHttpUrl(config.getDeleteMessageApiUrl())
                .buildAndExpand(threadId, aiMessageId)
                .toUriString();
        config.assistantTemplate().delete(aiMessageUrl);
    }

    @Transactional(readOnly = true)
    public List<ChatDetailResponse> getChatHistory(final Long chatRoomId) {
        final List<Message> historyMessages = messageRepository.findAllByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return historyMessages.stream()
                .map(ChatDetailResponse::of)
                .toList();
    }

    public void validateChatRoomByMember(final Long memberId, final Long chatRoomId) {
        if(!chatRoomRepository.existsByMemberIdAndId(memberId, chatRoomId)){
            throw new AuthException(NOT_FOUND_CHAT_ROOM);
        }
    }

    public void deleteChatRoom(final Long chatRoomId) {
        final ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new AuthException(NOT_FOUND_CHAT_ROOM));

        chatRoomRepository.delete(chatRoom);
        deleteThread(chatRoom.getThread());
    }

    private void deleteThread(final String threadId) {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getDeleteThreadApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        config.assistantTemplate().delete(url);
    }

    public void validateSummarizedChatRoom(final Long memberId, final Long chatRoomId) {
        final ChatRoom chatRoom = chatRoomRepository.findByMemberIdAndId(memberId, chatRoomId)
                .orElseThrow(() -> new AuthException(NOT_FOUND_CHAT_ROOM));
        if(!chatRoom.isSummarized()){
            throw new AuthException(NOT_FOUND_CHAT_ROOM);
        }
    }

    @Transactional(readOnly = true)
    public DetailNoteResponse getNote(final Long chatRoomId) {
        final Note note = noteRepository.findByChatRoomId(chatRoomId);
        return DetailNoteResponse.of(note);
    }
}
