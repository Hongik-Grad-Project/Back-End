package trackers.demo.chat.service;

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
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;

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

    private static final String DEFAULT_CHAT_ROOM_NAME = "새로운 채팅";
    private static final String AURORA_AI_CHAT_BOT = "채팅 AI";

    private final ChatGPTConfig config;

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final AssistantRepository assistantRepository;

    public Long createRoomV1(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new  BadRequestException(NOT_FOUND_MEMBER));

        final ChatRoom chatRoom = new ChatRoom(member, DEFAULT_CHAT_ROOM_NAME);
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
            final List<Message> messages = messageRepository.findAllByChatRoomId(chatRoomId);
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
        log.info("response = {}", response);
        return response;
    }

    // Thread 생성 로직
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
        final String receivedMessage = getMessage(chatRoom.getThread());

        messageRepository.save(new Message(chatRoom, receivedMessage, AURORA_AI));
        return ChatResponse.of(receivedMessage);
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
                return retrievedRun;
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

    private String getMessage(final String threadId) {
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
            ThreadMessageResponse.Message lastAssistantMessage = assistantMessages.get(0);
            return lastAssistantMessage.getContent().get(0).getText().getValue();
        } else {
            throw new BadRequestException(NOT_FOUND_MESSAGE_IN_THREAD);
        }
    }

}
