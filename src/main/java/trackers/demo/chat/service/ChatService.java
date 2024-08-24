package trackers.demo.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.admin.domain.Assistant;
import trackers.demo.admin.domain.repository.AssistantRepository;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.chat.domain.repository.MessageRepository;
import trackers.demo.chat.dto.request.CompletionMessage;
import trackers.demo.chat.dto.request.CompletionRequest;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.CompletionResponse;
import trackers.demo.chat.dto.response.ChatMessageResponse;
import trackers.demo.chat.dto.response.ThreadResponse;
import trackers.demo.global.config.ChatGPTConfig;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;

import java.util.*;

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

    public ChatMessageResponse createMessageV1(
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

        return ChatMessageResponse.of(receivedMessage);
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

    public ChatMessageResponse createMessageV2(
            final Long memberId,
            final Long chatRoomId,
            final CreateMessageRequest request) {

        // 사용자 메시지 생성하기 (Thread에 메시지를 넣는 작업)

        // Run 객체 실행 (응답을 생성하기 위한 작업)

        // 응답 상태 확인 -> 반복문 사용

        // 응답 메시지 추출

        return null;
    }


}
