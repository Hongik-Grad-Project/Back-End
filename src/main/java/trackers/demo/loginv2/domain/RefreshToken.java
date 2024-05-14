package trackers.demo.loginv2.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long memberId;
}
