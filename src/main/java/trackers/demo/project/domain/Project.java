package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import trackers.demo.member.domain.Member;
import trackers.demo.global.common.BaseEntity;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.domain.type.DonatedStatusType;
import trackers.demo.project.infrastructure.StringListConverter;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.EnumType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false ,columnDefinition = "tinyint(0) default 0")
    private boolean isRecruit;      // 팀원 모집 여부

    @Column(length = 200)
    private String wantedMember;    // 희망 팀원

    @Column(nullable = false)
    private LocalDate startDate;    // 프로젝트 시작 날짜

    @Column(nullable = false)
    private LocalDate endDate;  // 프로젝트 마감 날짜

    @Column(length = 50, nullable = false)
    private String projectTitle;    // 대표 사진

    @Column(nullable = false)
    private String mainImage;

    @Column(length = 300)
    @Convert(converter = StringListConverter.class)
    private List<String> subTitleList;

    @Column(length = 1000)
    @Convert(converter = StringListConverter.class)
    private List<String> contentList;

    @Column(length = 1000)
    @Convert(converter = StringListConverter.class)
    private List<String> projectImageList;

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
            final boolean isRecruit,
            final String wantedMember,
            final LocalDate startDate,
            final LocalDate endDate,
            final String projectTitle,
            final String mainImage,
            final List<String> subTitleList,
            final List<String> contentList,
            final List<String> projectImageList,
            final DonatedStatusType donatedStatus,
            final CompletedStatusType completedStatus,
            final int donatedAmount
    ) {
        this.id = id;
        this.member = member;
        this.isRecruit = isRecruit;
        this.wantedMember = wantedMember;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectTitle = projectTitle;
        this.mainImage = mainImage;
        this.subTitleList= subTitleList;
        this.contentList = contentList;
        this.projectImageList = projectImageList;
        this.donatedStatus = donatedStatus;
        this.completedStatus = completedStatus;
        this.donatedAmount = donatedAmount;
    }

    public static Project of(
            final Member member,
            final Boolean isRecruit,
            final String wantedMember,
            final LocalDate startDate,
            final LocalDate endDate,
            final String projectTitle,
            final String mainImage
    ) {
        return new Project(
                null,
                member,
                isRecruit,
                wantedMember,
                startDate,
                endDate,
                projectTitle,
                mainImage,
                null,
                null,
                null,
                DonatedStatusType.NOT_DONATED,
                CompletedStatusType.NOT_COMPLETED,
                0);
    }


}
