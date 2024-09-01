package trackers.demo.note.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.global.common.entity.BaseTimeEntity;
import trackers.demo.project.infrastructure.StringListConverter;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
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
    private List<String> subTitleList;

    @Column(length = 300)
    @Convert(converter = StringListConverter.class)
    private List<String> summaryList;

    private String solution;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
}
