package trackers.demo.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.gallery.domain.repository.CustomProjectRepository;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.project.domain.*;
import trackers.demo.project.domain.repository.*;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.dto.request.ProjectCreateOutlineRequest;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;

import java.util.*;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;


    private final ProjectTargetRepository projectTargetRepository;

    private final ProjectTagRepository projectTagRepository;

    private final TagRepository tagRepository;

    private final TargetRepository targetRepository;

    private final MemberRepository memberRepository;

    public Long saveProjectOutline(
            final Long memberId,
            final ProjectCreateOutlineRequest request,
            final String imageUrl
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 프로젝트 생성 및 저장
        final Project newProject = Project.of(
                member,
                request.getSummary(),
                request.getStartDate(),
                request.getEndDate(),
                request.getProjectTitle(),
                imageUrl);
        final Project project = projectRepository.save(newProject);

        // 프로젝트-대상 저장
        final Target target = targetRepository.findByTargetTitle(request.getTarget())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
        final ProjectTarget newProjectTarget = new ProjectTarget(null, project, target);
        projectTargetRepository.save(newProjectTarget);

        return project.getId();
    }

    public void saveProjectBody(
            final Long memberId,
            final Long projectId,
            final ProjectCreateBodyRequest createRequest,
            final List<String> imageUrlList
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 임시 저장된 프로젝트
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        // 태그 생성 및 프로젝트-태그 저장
        for(String tagTitle: createRequest.getTagList()){
            if(!tagRepository.existsByTagTitle(tagTitle)){
                final Tag newTag = new Tag(null, tagTitle);
                tagRepository.save(newTag);
            }
            final Tag tag = tagRepository.findByTagTitle(tagTitle)
                    .orElseThrow(() -> new  BadRequestException(NOT_FOUND_TAG));
            final ProjectTag newProjectTag = new ProjectTag(null, project, tag);
            projectTagRepository.save(newProjectTag);
        }

        // 프로젝트 생성 (소제목, 본문, 사진)
        project.saveProject(createRequest, imageUrlList);
        projectRepository.save(project);
    }

    public void validateProjectByMemberAndProjectId(
            final Long memberId,
            final Long projectId,
            final CompletedStatusType statusType) {
        if(!projectRepository.existsByMemberIdAndIdAndCompletedStatus(memberId, projectId, statusType)){
            throw new AuthException(INVALID_NOT_COMPLETED_PROJECT_WITH_MEMBER);
        }
    }
}

