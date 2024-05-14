package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trackers.demo.member.domain.Member;
import trackers.demo.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Target target;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(length = 50, nullable = false)
    private String donation_name;

    @Column(nullable = false)
    private String mainImage;

    @Column(length = 50, nullable = false)
    private String projectTitle;

    @Column(length = 4000)
    private String proposal;    // 프로젝트 소개

    @Column(columnDefinition = "tinyint(0) default 0")
    private boolean isRecruit;      // 팀원 모집 여부

    @Column(length = 200)
    private String wanted_member;

    @Column(columnDefinition = "tinyint(0) default 0")
    private boolean isDonate;      // 후원 여부

    private Long donated;   // 후원 금액
}
