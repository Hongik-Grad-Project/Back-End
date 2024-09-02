package trackers.demo.note.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.global.common.entity.BaseTimeEntity;
import trackers.demo.project.infrastructure.StringListConverter;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;
import static trackers.demo.global.common.entity.type.StatusType.USABLE;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE note SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status = 'USABLE'")
public class Note extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String target;

    @Column(nullable = false, length = 100)
    private String problem;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 180)
    @Convert(converter = StringListConverter.class)
    private List<String> openTitleList;     // 자유로운 제목

    @Column(length = 300)
    @Convert(converter = StringListConverter.class)
    private List<String> openSummaryList;   // 자유로운 요약 내용

    private String solution;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    public Note(
            final Long id,
            final String target,
            final String problem,
            final String title,
            final List<String> openTitleList,
            final List<String> openSummaryList,
            final String solution,
            final ChatRoom chatRoom
    ){
        this.id = id;
        this.target = target;
        this.problem = problem;
        this.title = title;
        this.openTitleList = openTitleList;
        this.openSummaryList = openSummaryList;
        this.solution = solution;
        this.chatRoom = chatRoom;
    }

    public static Note of(
            final String target,
            final String problem,
            final String title,
            final List<String> openTitleList,
            final List<String> openSummaryList,
            final String solution,
            final ChatRoom chatRoom
    ){
        return new Note(
                null,
                target,
                problem,
                title,
                openTitleList,
                openSummaryList,
                solution,
                chatRoom
        );
    }
}
