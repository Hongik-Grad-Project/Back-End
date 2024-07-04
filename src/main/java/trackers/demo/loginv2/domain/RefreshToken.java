package trackers.demo.loginv2.domain;

import jakarta.persistence.*;
import lombok.*;
import trackers.demo.global.common.entity.BaseEntity;

import static lombok.AccessLevel.*;


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

}
