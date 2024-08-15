package trackers.demo.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private static final Long DEFAULT_CHAT_ROOM_ID = null;
    private static final String DEFAULT_CHAT_ROOM_NAME = "새로운 채팅";

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public Long create(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new  BadRequestException(ExceptionCode.NOT_FOUND_MEMBER_ID));

        final ChatRoom chatRoom = new ChatRoom(member, DEFAULT_CHAT_ROOM_NAME);
        return chatRoomRepository.save(chatRoom).getId();
    }
}
