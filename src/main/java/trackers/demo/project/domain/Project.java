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
import trackers.demo.project.dto.request.ProjectCreateSecondRequest;
import trackers.demo.project.infrastructure.StringListConverter;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.EnumType.*;
import static trackers.demo.project.domain.type.CompletedStatusType.*;
import static trackers.demo.project.domain.type.DonatedStatusType.*;

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
    private String projectTitle;    // 프로젝트명

    @Column(nullable = false)
    private String mainImagePath;   // 대표 사진

    @Column(length = 200)
    @Convert(converter = StringListConverter.class)
    private List<String> subTitleList;

    @Column(length = 2000)
    @Convert(converter = StringListConverter.class)
    private List<String> contentList;

    @Column(length = 200)
    @Convert(converter = StringListConverter.class)
    private List<String> projectImageList;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private DonatedStatusType donatedStatus;      // 모집 전, 모집 중

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private CompletedStatusType completedStatus;    // 임시 저장 상태, 완료 상태

    @Column(nullable = false)
    private int donatedAmount = 0;   // 후원 받은 금액

    @Column(nullable = false)
    private int likes = 0;

    public Project(
            final Long id,
            final Member member,
            final boolean isRecruit,
            final String wantedMember,
            final LocalDate startDate,
            final LocalDate endDate,
            final String projectTitle,
            final String mainImagePath,
            final List<String> subTitleList,
            final List<String> contentList,
            final List<String> projectImageList,
            final DonatedStatusType donatedStatus,
            final CompletedStatusType completedStatus,
            final int donatedAmount,
            final int likes
    ) {
        this.id = id;
        this.member = member;
        this.isRecruit = isRecruit;
        this.wantedMember = wantedMember;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectTitle = projectTitle;
        this.mainImagePath = mainImagePath;
        this.subTitleList= subTitleList;
        this.contentList = contentList;
        this.projectImageList = projectImageList;
        this.donatedStatus = donatedStatus;
        this.completedStatus = completedStatus;
        this.donatedAmount = donatedAmount;
        this.likes = likes;
    }

    public static Project of(
            final Member member,
            final Boolean isRecruit,
            final String wantedMember,
            final LocalDate startDate,
            final LocalDate endDate,
            final String projectTitle,
            final String mainImagePath
    ) {
        return new Project(
                null,
                member,
                isRecruit,
                wantedMember,
                startDate,
                endDate,
                projectTitle,
                mainImagePath,
                null,
                null,
                null,
                NOT_DONATED,
                NOT_COMPLETED,
                0,
                0);
    }

    public void createProject(
            final ProjectCreateSecondRequest createRequest,
            final List<String> projectImageList
            ){
        this.subTitleList = createRequest.getTitleList();
        this.contentList = createRequest.getBodyList();
        this.projectImageList = projectImageList;
        this.completedStatus = COMPLETED;
    }


}
