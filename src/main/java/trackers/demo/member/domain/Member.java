package trackers.demo.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import trackers.demo.global.common.BaseEntity;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;
import static trackers.demo.member.domain.MemberState.ACTIVE;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 80)
    private String socialLoginId;

    @Column(nullable = false, length = 50)
    private String email;

    @Enumerated(value = STRING)
    private MemberState status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Member(final Long id, final String socialLoginId, final String email) {
        this.id = id;
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Member(final String socialLoginId, final String email){
        this(null, socialLoginId, email);
    }

}
