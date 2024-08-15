package trackers.demo.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.domain.type.SenderType;
import trackers.demo.global.common.entity.BaseCreateTimeEntity;
import trackers.demo.member.domain.Member;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Message extends BaseCreateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false, length = 1000)
    private String contents;

    @Enumerated(value = STRING)
    private SenderType senderType;

    public Message(final ChatRoom chatRoom, final String contents, final SenderType senderType){
        this.chatRoom = chatRoom;
        this.contents = contents;
        this.senderType = senderType;
    }

}
