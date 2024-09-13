package trackers.demo.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import trackers.demo.global.common.entity.BaseTimeEntity;
import trackers.demo.member.domain.Member;
import trackers.demo.note.domain.Note;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)

public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 30)
    private String chatRoomName;

    @Column(nullable = false)
    private String thread;

    @OneToMany(mappedBy = "chatRoom", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToOne(mappedBy = "chatRoom", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private Note note;

    @Column(nullable = false)
    private boolean isSummarized;

    public ChatRoom(final Member member, final String chatRoomName){
        this.member = member;
        this.chatRoomName = chatRoomName;
        this.isSummarized = false;
    }

    public ChatRoom(final Member member, final String chatRoomName, final String thread){
        this.member = member;
        this.chatRoomName = chatRoomName;
        this.thread = thread;
        this.isSummarized = false;
    }

    public void updateChatRoomName(final String chatRoomName){
        this.chatRoomName = chatRoomName;
        this.isSummarized = true;
    }

    public void updateMessages(final List<Message> messages) { this.messages = messages; }
}
