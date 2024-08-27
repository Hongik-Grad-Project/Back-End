package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Target {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String targetTitle;
}
