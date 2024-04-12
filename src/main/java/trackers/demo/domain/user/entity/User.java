package trackers.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import trackers.demo.global.common.BaseEntity;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String role;

    private String email;

    @Column(length = 20)
    private String username;    // 회원 실제 이름

    private String authkey;    // 회원을 식별할 수 있는 키

    // 거주지
    // 활동 지역

    public void setName(String username) {
        this.username = username;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
