package trackers.demo.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import trackers.demo.member.domain.Member;
import trackers.demo.global.common.entity.BaseEntity;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;
import trackers.demo.project.infrastructure.StringListConverter;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static trackers.demo.project.domain.type.CompletedStatusType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 20)
    private String summary;     // 사회 문제 요약

    @Column(nullable = false)
    private LocalDate startDate;    // 프로젝트 시작 날짜

    @Column(nullable = false)
    private LocalDate endDate;  // 프로젝트 마감 날짜

    @Column(length = 50, nullable = false)
    private String projectTitle;    // 프로젝트명

    @Column(nullable = false)
    private String mainImagePath;   // 대표 사진

    @Column(length = 180)
    @Convert(converter = StringListConverter.class)
    private List<String> subTitleList;

    @Column(length = 3000)
    @Convert(converter = StringListConverter.class)
    private List<String> contentList;

    @Column(length = 450)
    @Convert(converter = StringListConverter.class)
    private List<String> projectImageList;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private CompletedStatusType completedStatus;    // 임시 저장 상태, 완료 상태

    @Column(name = "is_deleted")
    private boolean deleted;

    public Project(
            final Long id,
            final Member member,
            final String summary,
            final LocalDate startDate,
            final LocalDate endDate,
            final String projectTitle,
            final String mainImagePath,
            final List<String> subTitleList,
            final List<String> contentList,
            final List<String> projectImageList,
            final CompletedStatusType completedStatus,
            final boolean deleted
    ) {
        this.id = id;
        this.member = member;
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectTitle = projectTitle;
        this.mainImagePath = mainImagePath;
        this.subTitleList= subTitleList;
        this.contentList = contentList;
        this.projectImageList = projectImageList;
        this.completedStatus = completedStatus;
        this.deleted = deleted;
    }

    public static Project of(
            final Member member,
            final String summary,
            final LocalDate startDate,
            final LocalDate endDate,
            final String projectTitle,
            final String mainImagePath
    ) {
        return new Project(
                null,
                member,
                summary,
                startDate,
                endDate,
                projectTitle,
                mainImagePath,
                null,
                null,
                null,
                NOT_COMPLETED,
                false);
    }

    public void saveProject(
            final ProjectCreateBodyRequest createRequest,
            final List<String> projectImageList
            ){
        this.subTitleList = createRequest.getSubtitleList();
        this.contentList = createRequest.getContentList();
        this.projectImageList = projectImageList;
    }


}
