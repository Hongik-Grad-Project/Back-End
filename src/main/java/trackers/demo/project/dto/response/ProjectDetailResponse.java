package trackers.demo.project.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.project.domain.*;
import trackers.demo.project.domain.type.DonatedStatusType;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDetailResponse {

    private final Long projectId;
    private final String projectTitle;
    private final String projectTarget;     // 프로젝트 대상
    private final String projectSubject;    // 프로젝트 주제
    private final String mainImagePath;
    private final List<String> subTitleList;
    private final List<String> contentList;
    private final List<String> projectImageList;
    private final String wantedMember;
    private final DonatedStatusType donatedStatus;
    private final int donatedAmount;
    private final int likes;
    // 태그 추가 필요

    public static ProjectDetailResponse projectDetail(
            final Project project,
            final Target target,
            final Subject subject){
        return new ProjectDetailResponse(
                project.getId(),
                project.getProjectTitle(),
                target.getTargetTitle(),
                subject.getSubjectTitle(),
                project.getMainImagePath(),
                project.getSubTitleList(),
                project.getContentList(),
                project.getProjectImageList(),
                project.getWantedMember(),
                project.getDonatedStatus(),
                project.getDonatedAmount(),
                project.getLikes()
        );
    }

}
