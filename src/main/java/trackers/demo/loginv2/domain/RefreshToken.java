package trackers.demo.loginv2.domain;

import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.*;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long memberId;
}
