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
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.dto.request.ProjectCreateSecondRequest;
import trackers.demo.project.dto.response.ProjectDetailResponse;

import java.util.List;

import static trackers.demo.global.exception.ExceptionCode.*;
import static trackers.demo.project.domain.type.CompletedStatusType.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTargetRepository projectTargetRepository;
    private final TargetRepository targetRepository;
    private final ProjectSubjectRepository projectSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final MemberRepository memberRepository;

    public void saveProjectFirst(
            final Long memberId,
            final ProjectCreateFirstRequest request,
            final String imageUrl
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 프로젝트 생성 및 저장
        final Project newProject = Project.of(
                member,
                request.getIsRecruit(),
                request.getWantedMember(),
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

        // 프로젝트-주제 저장
        final Subject subject = subjectRepository.findBySubjectTitle(request.getSubject())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBJECT));
        final ProjectSubject newProjectSubject = new ProjectSubject(null, project, subject);
        projectSubjectRepository.save(newProjectSubject);

    }

    public Long saveProjectSecond(
            final Long memberId,
            final ProjectCreateSecondRequest createRequest,
            final List<String> imageUrlList
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 임시 저장된 프로젝트
        final Project project = projectRepository.findByMemberAndCompletedStatus(member, NOT_COMPLETED);

        // 프로젝트 생성 (소제목, 본문, 사진)
        project.createProject(createRequest, imageUrlList);
        projectRepository.save(project);

        return project.getId();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(final Long projectId) {
        final Project project = projectRepository.getReferenceById(projectId);
        final ProjectTarget projectTarget = projectTargetRepository.findByProject(project);
        final Target target = targetRepository.getReferenceById(projectTarget.getTarget().getId());
        final ProjectSubject projectSubject = projectSubjectRepository.findByProject(project);
        final Subject subject = subjectRepository.getReferenceById(projectSubject.getSubject().getId());
        return ProjectDetailResponse.projectDetail(project,target,subject);
    }

    public void validateProjectByMember(final Long memberId, final CompletedStatusType statusType) {
        if(!projectRepository.existsByMemberIdAndCompletedStatus(memberId, statusType)){
            throw new AuthException(INVALID_NOT_COMPLETED_PROJECT_WITH_MEMBER);
        }
    }

    public void validateProjectByProjectId(final Long projectId){
        if(!projectRepository.existsById(projectId)){
            throw new AuthException(NOT_FOUND_PROJECT);
        }
    }


}
