package trackers.demo.loginv2.domain;

import jakarta.persistence.*;
import lombok.*;
import trackers.demo.global.common.BaseEntity;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;
import static trackers.demo.member.domain.MemberState.ACTIVE;


@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long memberId;

    public RefreshToken(Long id, String token, Long memberId) {
        this.id = id;
        this.token = token;
        this.memberId = memberId;
    }

    public RefreshToken(String token, Long memberId) {
        this(null, token, memberId);
    }

    //    public Member(final Long id, final String socialLoginId, final String email) {
//        this.id = id;
//        this.socialLoginId = socialLoginId;
//        this.email = email;
//        this.status = ACTIVE;
//        this.createdAt = LocalDateTime.now();
//        this.modifiedAt = LocalDateTime.now();
//    }
//
//    public Member(final String socialLoginId, final String email){
//        this(null, socialLoginId, email);
//    }
}
