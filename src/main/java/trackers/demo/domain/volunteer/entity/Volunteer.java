package trackers.demo.domain.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import trackers.demo.global.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Volunteer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Long id;

    private String title;   // 봉사 제목

    private String period;  // 활동 기간 -> 시작 기간, 끝 기간 나누기!

}
