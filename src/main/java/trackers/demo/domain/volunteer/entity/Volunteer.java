package trackers.demo.domain.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import trackers.demo.global.common.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Volunteer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vol_id")
    private Long id;

    private String title;   // 봉사 제목

    private LocalDate startDate;     // 활동 시작 날짜

    private LocalDate endDate;     // 활동 종료 날짜

    private String activityCycle;      // 활동 주기

    private String area;        // 봉사 지역

    private String location;    // 봉사 장소

    private String target;      // 봉사 대상

    private int numApplicant;   // 신청 인원

    private int numRequired;    // 필요 인원

    private String imageUrl;

    private String majorCategory;   // 대분류

    private String subCategory;     // 소분류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organiztion_id")
    private Organization organization;

}
