package trackers.demo.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.chat.domain.repository.MessageRepository;
import trackers.demo.chat.domain.type.ChatMessageRole;
import trackers.demo.chat.dto.request.ChatGPTMessage;
import trackers.demo.chat.dto.request.ChatGPTRequest;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.ChatGPTResponse;
import trackers.demo.chat.dto.response.ChatMessageResponse;
import trackers.demo.global.config.ChatGPTConfig;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

import static trackers.demo.chat.domain.type.ChatMessageRole.*;
import static trackers.demo.chat.domain.type.SenderType.*;
import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private static final String DEFAULT_CHAT_ROOM_NAME = "새로운 채팅";

    private final ChatGPTConfig config;

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    public Long create(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new  BadRequestException(NOT_FOUND_MEMBER));

        final ChatRoom chatRoom = new ChatRoom(member, DEFAULT_CHAT_ROOM_NAME);
        return chatRoomRepository.save(chatRoom).getId();
    }

    public ChatMessageResponse createMessage(
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
        ChatGPTRequest request;
        if(!messageRepository.existsByChatRoomId(chatRoomId)){      // 처음 텍스트 생성 (이전 채팅 내역 존재X)
            request = new ChatGPTRequest(config.getModel(), prompt);
        } else {    // 이전 채팅 내역 추가
            final List<ChatGPTMessage> chatGPTMessages = new ArrayList<>();
            final List<Message> messages = messageRepository.findAllByChatRoomId(chatRoomId);
            for(final Message message : messages){
                ChatGPTMessage historyMessage = null;
                if(message.getSenderType().equals(AURORA_AI)){
                    historyMessage = new ChatGPTMessage(ASSISTANT.value(), message.getContents());
                } else if (message.getSenderType().equals(MEMBER)) {
                    historyMessage = new ChatGPTMessage(USER.value(), message.getContents());
                }
                chatGPTMessages.add(historyMessage);
            }
            request = new ChatGPTRequest(config.getModel(), chatGPTMessages, prompt);
        }

        // todo: 프롬프트 추가 { role: "system", content: "You are a helpful assistant." }
        log.info("RestTemplate으로 ChatGPT API에 POST 요청");
        ChatGPTResponse chatGPTResponse = config.template().postForObject(config.getApiURL(), request, ChatGPTResponse.class);
        log.info("응답 받기 성공");
        String response = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        log.info("response = {}", response);
        return response;
    }
}