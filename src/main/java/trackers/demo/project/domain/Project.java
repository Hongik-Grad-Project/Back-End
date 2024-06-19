package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import trackers.demo.member.domain.Member;
import trackers.demo.global.common.BaseEntity;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.domain.type.DonatedStatusType;

import static jakarta.persistence.EnumType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 50)
    private String donationName;

    @Column(nullable = false)
    private String mainImage;

    @Column(length = 50, nullable = false)
    private String projectTitle;

    @Column(length = 3000)
    private String proposal;    // 프로젝트 소개

    @Column(columnDefinition = "tinyint(0) default 0")
    private boolean isRecruit;      // 팀원 모집 여부

    @Column(length = 200)
    private String wantedMember;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private DonatedStatusType donatedStatus;      // 모집 전, 모집 중

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private CompletedStatusType completedStatus;    // 임시 저장 상태, 완료 상태

    private int donatedAmount;   // 후원 금액

    public Project(
            final Long id,
            final Member member,
            final String donationName,
            final String mainImage,
            final String projectTitle,
            final String proposal,
            final boolean isRecruit,
            final String wantedMember,
            final DonatedStatusType donatedStatus,
            final CompletedStatusType completedStatus,
            final int donatedAmount
    ) {
        this.id = id;
        this.member = member;
        this.donationName = donationName;
        this.mainImage = mainImage;
        this.projectTitle = projectTitle;
        this.proposal = proposal;
        this.isRecruit = isRecruit;
        this.wantedMember = wantedMember;
        this.donatedStatus = donatedStatus;
        this.completedStatus = completedStatus;
        this.donatedAmount = donatedAmount;
    }

    public static Project of(
            final Member member,
            final String projectTitle,
            final String mainImage
    ) {
        return new Project(
                null,
                member,
                null,
                mainImage,
                projectTitle,
                null,
                false,
                null,
                DonatedStatusType.NOT_DONATED,
                CompletedStatusType.NOT_COMPLETED,
                0);
    }


}
