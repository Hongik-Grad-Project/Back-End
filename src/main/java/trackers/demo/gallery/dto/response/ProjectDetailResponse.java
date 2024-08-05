package trackers.demo.gallery.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.member.domain.Member;
import trackers.demo.project.domain.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDetailResponse {

    private final Long projectId;

    private final String projectTarget;     // 프로젝트 대상

    private final LocalDate startDate;

    private final String projectTitle;

    private final Long likeCount;

    private final String mainImagePath;

    private final List<String> projectTag;    // 프로젝트 태그

    private final List<String> subTitleList;

    private final List<String> contentList;

    private final List<String> projectImageList;

    private final boolean isLike;

    private final String memberName;

    private final String memberEmail;

    private final String memberIntro;


    // todo: 오로라 다른 글 목록 보기 추가


    public static ProjectDetailResponse projectDetail(
            final Project project,
            final List<String> tagList,
            final String targetName,
            final Boolean isLike,
            final Long likeCount,
            final Member projectOwner
    ){
        return new ProjectDetailResponse(
                project.getId(),
                targetName,
                project.getStartDate(),
                project.getProjectTitle(),
                likeCount,
                project.getMainImagePath(),
                tagList,
                project.getSubTitleList(),
                project.getContentList(),
                project.getProjectImageList(),
                isLike,
                projectOwner.getNickname(),
                projectOwner.getEmail(),
                projectOwner.getIntroduction()
        );
    }

}
