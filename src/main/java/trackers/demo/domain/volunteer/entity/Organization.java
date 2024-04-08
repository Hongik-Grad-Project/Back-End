package trackers.demo.domain.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import trackers.demo.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long id;

    @Column(length = 30)
    private String name;

    @Column(columnDefinition = "float default 0")
    private float ratingSum;    // 별점 총점

    @Column(columnDefinition = "integer default 0")
    private int reviewsCnt;     // 리뷰 수
}
