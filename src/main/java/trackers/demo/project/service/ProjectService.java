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

    public void save(final Long memberId, final ProjectCreateFirstRequest request, final String imageURL) {
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
                imageURL);
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
}
