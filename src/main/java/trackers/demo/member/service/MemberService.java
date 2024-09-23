package trackers.demo.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.gallery.domain.repository.CustomProjectRepository;
import trackers.demo.gallery.dto.response.ProjectResponse;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.domain.repository.LikeRepository;
import trackers.demo.like.dto.LikeElement;
import trackers.demo.like.dto.LikeInfo;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.member.dto.request.MyProfileUpdateRequest;
import trackers.demo.member.dto.response.LikeProjectResponse;
import trackers.demo.member.dto.response.MyPageResponse;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTarget;
import trackers.demo.project.domain.Target;
import trackers.demo.project.domain.repository.ProjectRepository;
import trackers.demo.project.domain.repository.ProjectTargetRepository;
import trackers.demo.project.domain.repository.TargetRepository;

import java.util.*;

import static trackers.demo.global.exception.ExceptionCode.*;
import static trackers.demo.like.domain.LikeRedisConstants.EMPTY_MARKER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private static final int FIXED_AMOUNT = 3;

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final ProjectRepository projectRepository;
    private final CustomProjectRepository customProjectRepository;
    private final ProjectTargetRepository projectTargetRepository;
    private final TargetRepository targetRepository;
    private final CustomLikeRepository customLikeRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public void validateProfileByMember(final Long memberId) {
        if(!memberRepository.existsById(memberId)){
            throw new BadRequestException(NOT_FOUND_MEMBER);
        }
    }

    public void updateProfile(
            final Long memberId,
            final MyProfileUpdateRequest updateRequest,
            final String newImageUrl
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        String persistImageUrl = member.getProfileImage();

        if(newImageUrl != null){
            persistImageUrl = newImageUrl;
            // todo: 기존 프로필 이미지 S3에서도 지우기
        }

        member.updateProfile(
                updateRequest.getNickname(),
                newImageUrl,
                updateRequest.getIntroduction()
        );
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        final Pageable pageable = Pageable.ofSize(FIXED_AMOUNT);
        // 나의 프로젝트 조회
        final List<ProjectResponse> myProjectResponses = getProjectResponses(memberId, pageable);
        // 내가 응원한 프로젝트 조회
        final List<LikeProjectResponse> likeProjectResponses = getLikeProjectResponses(memberId, pageable);

        return MyPageResponse.of(member, myProjectResponses, likeProjectResponses);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(final Long memberId) {
        List<ProjectResponse> myProjectResponses = new ArrayList<>();

        final List<Project> myProjects = projectRepository.findProjectsByMemberId(memberId);
        final List<Long> projectIds = myProjects.stream().map(Project::getId).toList();
        final Map<Long, String> targetNameByProject = getTargetNameByProject(projectIds);
        final Map<Long, LikeInfo> likeInfosByProject = getLikeInfosByProject(projectIds, memberId);

        for(final Project project : myProjects){
            final ProjectResponse projectResponse = ProjectResponse.of(
                    project,
                    targetNameByProject.get(project.getId()),
                    likeInfosByProject.get(project.getId()).isLike(),
                    likeInfosByProject.get(project.getId()).getLikeCount()
            );
            myProjectResponses.add(projectResponse);
        }
        return myProjectResponses;
    }

    @Transactional(readOnly = true)
    public List<LikeProjectResponse> getLikeProjects(final Long memberId) {
        List<LikeProjectResponse> likeProjectResponses = new ArrayList<>();

        final List<Project> likeProjects = customProjectRepository.findLikedProjects(memberId);
        final List<Long> projectIds = likeProjects.stream().map(Project::getId).toList();
        final Map<Long, String> targetNameByProject = getTargetNameByProject(projectIds);

        for(final Project project : likeProjects){
            final LikeProjectResponse likeProjectResponse = LikeProjectResponse.of(
                    project,
                    targetNameByProject.get(project.getId())
            );
            likeProjectResponses.add(likeProjectResponse);
        }
        return likeProjectResponses;
    }

    private List<ProjectResponse> getProjectResponses(final Long memberId, final Pageable pageable) {
        List<ProjectResponse> myProjectResponses = new ArrayList<>();

        final List<Project> myProjects = customProjectRepository.getMyRecentProjects(memberId, pageable);
        final List<Long> projectIds = myProjects.stream().map(Project::getId).toList();
        final Map<Long, String> targetNameByProject = getTargetNameByProject(projectIds);
        final Map<Long, LikeInfo> likeInfosByProject = getLikeInfosByProject(projectIds, memberId);

        for(final Project project : myProjects){
            final ProjectResponse projectResponse = ProjectResponse.of(
                    project,
                    targetNameByProject.get(project.getId()),
                    likeInfosByProject.get(project.getId()).isLike(),
                    likeInfosByProject.get(project.getId()).getLikeCount()
            );
            myProjectResponses.add(projectResponse);
        }
        return myProjectResponses;
    }

    private List<LikeProjectResponse> getLikeProjectResponses(final Long memberId, final Pageable pageable) {

        List<LikeProjectResponse> likeProjectResponses = new ArrayList<>();

        final List<Project> likeProjects = customProjectRepository.findLikedProjects(memberId, pageable);
        final List<Long> projectIds = likeProjects.stream().map(Project::getId).toList();
        final Map<Long, String> targetNameByProject = getTargetNameByProject(projectIds);

        for(final Project project : likeProjects){
            final LikeProjectResponse likeProjectResponse = LikeProjectResponse.of(
                    project,
                    targetNameByProject.get(project.getId())
            );
            likeProjectResponses.add(likeProjectResponse);
        }
        return likeProjectResponses;
    }

    private Map<Long, String> getTargetNameByProject(final List<Long> projectIds) {
        final Map<Long, String> targetNameByProject = new HashMap<>();

        for(final Long projectId : projectIds){
            final ProjectTarget projectTarget = projectTargetRepository.findByProjectId(projectId);
            final Target target = targetRepository.findById(projectTarget.getTarget().getId())
                    .orElseThrow(() -> new  BadRequestException(NOT_FOUND_TARGET));
            targetNameByProject.put(projectId, target.getTargetTitle());
        }
        return targetNameByProject;
    }

    private Map<Long, LikeInfo> getLikeInfosByProject(final List<Long> projectIds, final Long memberId) {
        final Map<Long, LikeInfo> likeInfosByProject = new HashMap<>();

        for(final Long projectId : projectIds){
            final LikeElement likeElement = customLikeRepository.findLikesElementByProjectId(projectId)
                    .orElse(LikeElement.empty(projectId));
            final LikeInfo likeInfo = new LikeInfo(likeElement.getLikeCount(), likeElement.isLike(memberId));
            likeInfosByProject.put(projectId, likeInfo);
        }
        return likeInfosByProject;
    }
}
