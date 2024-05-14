package trackers.demo.login.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authkey;

    @Column(length = 3000)
    private String refresh;

    private String expiration;
}
