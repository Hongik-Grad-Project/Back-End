package trackers.demo.chat.fixture;

import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.type.SenderType;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.ChatDetailResponse;
import trackers.demo.chat.dto.response.ChatResponse;
import trackers.demo.chat.dto.response.ChatRoomResponse;

import java.time.LocalDateTime;
import java.util.List;

import static trackers.demo.member.fixture.MemberFixture.DUMMY_MEMBER;

public class ChatFixture {

    public static ChatRoom DUMMY_CHAT_ROOM = new ChatRoom(
            1L,
            DUMMY_MEMBER,
            "새로운 채팅방",
            null,
            null,
            null,
            false
    );


    public static final Message MEMBER_MESSAGE1 = new Message(
            DUMMY_CHAT_ROOM,
            "안녕, 사회 문제에 대해 이야기 하고 싶어",
            SenderType.MEMBER
    );

    public static final Message AI_MESSAGE1 = new Message(
            DUMMY_CHAT_ROOM,
            "안녕하세요 오로라AI 입니다. 해결 하고 싶은 사회 문제가 무엇인가요?",
            SenderType.AURORA_AI
    );

    public static final CreateMessageRequest DUMMY_CHAT_MESSAGE_REQUEST = new CreateMessageRequest(
            "안녕, 사회 문제에 대해 이야기 하고 싶어"
    );

    public static final ChatResponse DUMMY_CHAT_MESSAGE_RESPONSE = new ChatResponse(
            "안녕하세요 오로라AI 입니다. 해결 하고 싶은 사회 문제가 무엇인가요?"
    );

    static {
        DUMMY_CHAT_ROOM.updateMessages(List.of(MEMBER_MESSAGE1, AI_MESSAGE1));
    }

    public static final ChatRoomResponse DUMMY_CHAT_ROOM_1 = new ChatRoomResponse(
            1L,
            "새로운 채팅1",
            false,
            LocalDateTime.now()
    );

    public static final ChatRoomResponse DUMMY_CHAT_ROOM_2 = new ChatRoomResponse(
            2L,
            "새로운 채팅2",
            false,
            LocalDateTime.now()
    );

    public static final ChatRoomResponse DUMMY_CHAT_ROOM_3 = new ChatRoomResponse(
            3L,
            "새로운 채팅3",
            true,
            LocalDateTime.now()
    );

    public static final ChatDetailResponse DUMMY_CHAT_DETAIL_RESPONSE_MEMBER = new ChatDetailResponse(
            "안녕 나는 사회 문제에 관심이 많은 예비 창업가야",
            SenderType.MEMBER,
            LocalDateTime.now()
    );

    public static final ChatDetailResponse DUMMY_CHAT_DETAIL_RESPONSE_AI = new ChatDetailResponse(
            "안녕하세요! 사회 문제 해결에 관심이 많으신가요? 어떤 문제에 가장 큰 관심이 있으신가요?",
            SenderType.AURORA_AI,
            LocalDateTime.now()
    );

}
