package trackers.demo.chat.fixture;

import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.type.SenderType;
import trackers.demo.chat.dto.request.CreateMessageRequest;
import trackers.demo.chat.dto.response.ChatMessageResponse;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.project.domain.type.CompletedStatusType;

import java.time.LocalDate;
import java.util.List;

import static trackers.demo.member.fixture.MemberFixture.DUMMY_MEMBER;

public class ChatFixture {

    public static ChatRoom DUMMY_CHAT_ROOM = new ChatRoom(
            1L,
            DUMMY_MEMBER,
            "새로운 채팅방",
            null,
            null,
            null
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

    public static final ChatMessageResponse DUMMY_CHAT_MESSAGE_RESPONSE = new ChatMessageResponse(
            "안녕하세요 오로라AI 입니다. 해결 하고 싶은 사회 문제가 무엇인가요?"
    );

    static {
        DUMMY_CHAT_ROOM.updateMessages(List.of(MEMBER_MESSAGE1, AI_MESSAGE1));
    }

}
