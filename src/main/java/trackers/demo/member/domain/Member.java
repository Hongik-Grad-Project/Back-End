package trackers.demo.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;
import static trackers.demo.member.domain.MemberState.ACTIVE;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status = 'ACTIVE'")
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String socialLoginId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(length = 100)
    private String introduction;

    @Enumerated(value = STRING)
    private MemberState status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Member(final Long id, final String socialLoginId, final String nickname, final String email) {
        this.id = id;
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.nickname = nickname;
        this.introduction = "아직 한 줄 소개를 기입하지 않았습니다";
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        // todo: 멤버 한줄 소개 추가 (랜덤)
    }

    public Member(final String socialLoginId, final String nickname, final String email){
        this(null, nickname, socialLoginId, email);
    }

}
