package trackers.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50) default 'ROLE_USER'")
    private Role role;

    @Column(nullable = false)
    private String authKey;

    @Column(length = 20)
    private String nickname;

    // 거주지
    // 활동 지역

}
