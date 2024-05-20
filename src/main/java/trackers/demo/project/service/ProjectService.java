package trackers.demo.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.project.domain.*;
import trackers.demo.project.domain.repository.*;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTargetRepository projectTargetRepository;
    private final TargetRepository targetRepository;
    private final ProjectSubjectRepository projectSubjectRepository;
    private final SubjectRepository subjectRepository;

    private final MemberRepository memberRepository;

    public Long save(final Long memberId, final ProjectCreateFirstRequest projectCreateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 프로젝트 생성
        final Project newProject = Project.of(
                member,
                projectCreateRequest.getDonationName(),
                projectCreateRequest.getMainImage()
        );

        // 프로젝트 저장
        final Project project = projectRepository.save(newProject);

        // 프로젝트 대상 저장
        final String targetName = projectCreateRequest.getTarget();
        final Target target = targetRepository.findByTargetTitle(targetName)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
        final ProjectTarget newProjectTarget = new ProjectTarget(null, project, target);
        projectTargetRepository.save(newProjectTarget);

        // 프로젝트 주제 저장
        final String subjectName = projectCreateRequest.getSubject();
        final Subject subject = subjectRepository.findBySubjectTitle(subjectName)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBJECT));
        final ProjectSubject newProjectSubject = new ProjectSubject(null, project, subject);
        projectSubjectRepository.save(newProjectSubject);

        return project.getId();
    }
}
