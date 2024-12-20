package trackers.demo.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.global.exception.AuthException;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.project.domain.*;
import trackers.demo.project.domain.repository.*;
import trackers.demo.project.domain.type.CompletedStatusType;
import trackers.demo.project.dto.request.ProjectCreateOutlineRequest;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;
import trackers.demo.project.dto.request.ProjectUpdateBodyRequest;
import trackers.demo.project.dto.request.ProjectUpdateOutlineRequest;
import trackers.demo.project.dto.response.ProjectBodyResponse;
import trackers.demo.project.dto.response.ProjectOutlineResponse;
import trackers.demo.project.dto.response.SaveProjectResponse;

import java.util.*;
import java.util.stream.Collectors;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {

    private static final String DEFAULT_IMAGE_URL = "https://image.myaurora.co.kr/dev/a0606c166df331b54f8731caef9bbe5cc9b953f57586e2dc1ecdd73d85586cae.png";

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
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        String persistImageUrl = DEFAULT_IMAGE_URL;
        if (imageUrl != null) {
            persistImageUrl = imageUrl;
        }

        // 프로젝트 생성 및 저장
        final Project project = Project.projectOutline(
                member,
                request.getSummary(),
                request.getStartDate(),
                request.getEndDate(),
                request.getProjectTitle(),
                persistImageUrl);
        final Project newProject = projectRepository.save(project);

        // 프로젝트-대상 저장
        final Target target = targetRepository.findByTargetTitle(request.getTarget())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
        final ProjectTarget newProjectTarget = new ProjectTarget(null, newProject, target);
        projectTargetRepository.save(newProjectTarget);

        return newProject.getId();
    }

    public void saveProjectBody(
            final Long memberId,
            final Long projectId,
            final ProjectCreateBodyRequest createRequest,
            final List<String> imageUrlList
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

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
        project.projectBody(createRequest, imageUrlList);
        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public ProjectOutlineResponse getProjectOutline(final Long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        final ProjectTarget projectTarget = projectTargetRepository.findByProjectId(projectId);
        final Target target = targetRepository.getReferenceById(projectTarget.getTarget().getId());
        final String targetName = target.getTargetTitle();

        return ProjectOutlineResponse.of(project, targetName);
    }

    @Transactional(readOnly = true)
    public ProjectBodyResponse getProjectBody(final Long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        // 프로젝트 태그
        final List<ProjectTag> projectTagList = projectTagRepository.findAllByProject(project);
        final ArrayList<String> tagList = new ArrayList<>();
        for(final ProjectTag projectTag: projectTagList){
            Tag tag = tagRepository.findById(projectTag.getTag().getId())
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_TAG));
            tagList.add(tag.getTagTitle());
        }

        return ProjectBodyResponse.of(project, tagList);
    }

    public void updateProjectOutline(
            final Long projectId,
            final ProjectUpdateOutlineRequest updateRequest,
            final String newImageUrl
    ) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        String persistImageUrl = project.getMainImagePath();
        String persistTarget = projectTargetRepository.findByProjectId(projectId).getTarget().getTargetTitle();

        // 이미지 URL 업데이트
        if (newImageUrl != null) {
            persistImageUrl = newImageUrl;
            // todo: 기존 이미지 S3 에서도 지우기
        }

        // 프로젝트 대상 업데이트
        if(!persistTarget.equals(updateRequest.getTarget())){
            updateProjectTarget(project, updateRequest.getTarget());
        }

        // 프로젝트 업데이트
        project.updateOutline(
                updateRequest.getSummary(),
                updateRequest.getStartDate(),
                updateRequest.getEndDate(),
                updateRequest.getProjectTitle(),
                persistImageUrl
        );
        projectRepository.save(project);

    }

    private void updateProjectTarget(final Project project, final String updatedTarget) {
        projectTargetRepository.deleteByProjectId(project.getId());
        final Target target = targetRepository.findByTargetTitle(updatedTarget)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
        final ProjectTarget newProjectTarget = new ProjectTarget(null, project, target);
        projectTargetRepository.save(newProjectTarget);
    }

    public void updateProjectBody(
            final Long projectId,
            final ProjectUpdateBodyRequest updateRequest,
            final List<String> newImageUrlList
    ) {
       final Project project = projectRepository.findById(projectId)
               .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

       List<String> persistImageUrlList = null;
       List<String> persistTagList = null;

       // 이미지 업데이트
       if(newImageUrlList != null){
           persistImageUrlList = updateProjectImageUrls(updateRequest.getProjectImageList(), newImageUrlList);
           // todo: 기존 이미지 S3 에서 지우는 로직 추가
       }

        // 프로젝트 태그 업데이트
        if(!updateRequest.getTagList().isEmpty()){
            updateProjectTags(project, updateRequest.getTagList());
        }

        // 프로젝트 업데이트
        project.updateBody(
                updateRequest.getSubtitleList(),
                updateRequest.getContentList(),
                persistImageUrlList
        );
        projectRepository.save(project);
    }

    private List<String> updateProjectImageUrls(final List<String> storedImageList, final List<String> newImageList) {
        ArrayList<String> persistImages = new ArrayList<>();
        if(storedImageList != null){
            persistImages.addAll(storedImageList);  // 변경 사항이 없는 이미지 URL 추가
        }
        if(newImageList != null){
            persistImages.addAll(newImageList);     // 새로 추가된 이미지 URL 추가
        }
        return persistImages;
    }

    private void updateProjectTags(final Project project, final List<String> newTagList) {
        final List<ProjectTag> projectTagList = projectTagRepository.findAllByProject(project);
        final ArrayList<String> storedTagList = projectTagList.stream()
                .map(projectTag -> tagRepository.findById(projectTag.getTag().getId())
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_TAG)))
                .map(Tag::getTagTitle)
                .collect(Collectors.toCollection(ArrayList::new));

        // 기존 태그 중 새로운 태그에 포함되지 않은 태그 삭제
        for(final String storedTag : storedTagList){
            if(!newTagList.contains(storedTag)){    // 새로운 태그
                final Tag tag = tagRepository.findByTagTitle(storedTag).
                        orElseThrow(() -> new BadRequestException(NOT_FOUND_TAG));
                projectTagRepository.deleteByProjectAndTag(project, tag);
            }
        }

        // 새로운 태그 중 기존 태그에 포함되지 않는 태그 저장
        for(final String newTag : newTagList){
            if(!storedTagList.contains(newTag)){
                if(!tagRepository.existsByTagTitle(newTag)){     // 새로운 태그가 존재하지 않음
                    tagRepository.save(new Tag(null, newTag));
                }
                final Tag tag = tagRepository.findByTagTitle(newTag)
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_TAG));
                projectTagRepository.save(new ProjectTag(null, project, tag));
            }
        }
    }

    public void registerProject(final Long projectId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROJECT));

        project.updateCompletedStatus(CompletedStatusType.COMPLETED);
        projectRepository.save(project);
    }

    public void delete(final Long projectId) {
        projectTargetRepository.deleteByProjectId(projectId);
        projectTagRepository.deleteAllByProjectId(projectId);
        projectRepository.deleteById(projectId);
    }

    public void validateProjectByMemberAndProjectStatus(
            final Long memberId,
            final Long projectId,
            final CompletedStatusType statusType) {
        if(!projectRepository.existsByMemberIdAndIdAndCompletedStatus(memberId, projectId, statusType)){
            throw new AuthException(INVALID_NOT_COMPLETED_PROJECT_WITH_MEMBER);
        }
    }

    public void validateProjectByMember(final Long memberId, final Long projectId) {
        if(!projectRepository.existsByMemberIdAndId(memberId, projectId)){
            throw new AuthException(INVALID_PROJECT_WITH_MEMBER);
        }
    }
}

