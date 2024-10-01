package trackers.demo.project.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.project.dto.request.ProjectCreateOutlineRequest;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;
import trackers.demo.project.dto.request.ProjectUpdateBodyRequest;
import trackers.demo.project.dto.request.ProjectUpdateOutlineRequest;
import trackers.demo.project.dto.response.ProjectBodyResponse;
import trackers.demo.project.dto.response.ProjectOutlineResponse;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.net.URI;
import java.util.List;

import static trackers.demo.project.domain.type.CompletedStatusType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ImageService imageService;

    @PostMapping("/outline/save")
    @MemberOnly
    public ResponseEntity<Void> saveProjectOutline(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final ProjectCreateOutlineRequest createRequest,
            @RequestPart(value = "file") final MultipartFile mainImage
            ){
        log.info("memberId={}의 프로젝트 개요 저장 요청이 들어왔습니다.", accessor.getMemberId());
        final String imageUrl = imageService.saveImage(mainImage);
        final Long projectId = projectService.saveProjectOutline(accessor.getMemberId(), createRequest ,imageUrl);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }

    @PostMapping("/{projectId}/body/save")
    @MemberOnly
    public ResponseEntity<Void> saveProjectBody(
            @Auth final Accessor accessor,
            @PathVariable(name = "projectId") final Long projectId,
            @RequestPart(value = "dto") @Valid final ProjectCreateBodyRequest createRequest,
            @RequestPart(value = "files") final List<MultipartFile> images
            ){
        log.info("memberId={}의 projectId={} 본문 저장 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);
        final List<String> imageUrlList = imageService.saveImages(images);
        projectService.saveProjectBody(accessor.getMemberId(), projectId, createRequest, imageUrlList);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }

    @GetMapping("/{projectId}/outline")
    @MemberOnly
    public ResponseEntity<ProjectOutlineResponse> getProjectOutline(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId
    ){
        log.info("memberId={}의 projectId={}의 개요 조회 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);
        final ProjectOutlineResponse projectOutlineResponse = projectService.getProjectOutline(projectId);
        return ResponseEntity.ok().body(projectOutlineResponse);
    }

    @PostMapping("/{projectId}/outline/edit")
    @MemberOnly
    public ResponseEntity<Void> updateProjectOutline(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId,
            @RequestPart(value = "dto") @Valid final ProjectUpdateOutlineRequest updateRequest,
            @RequestPart(value = "file", required = false) final MultipartFile mainImage
    ){
        log.info("memberId={}의 projectId={} 개요 수정 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);

        String imageUrl = null;
        if(mainImage != null && !mainImage.isEmpty()){    // 프로젝트 대표사진에 수정이 있음
            imageUrl = imageService.saveImage(mainImage);
        }

        projectService.updateProjectOutline(projectId, updateRequest, imageUrl);

        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }

    @GetMapping("/{projectId}/body")
    @MemberOnly
    public ResponseEntity<ProjectBodyResponse> getProjectBody(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId
    ){
        log.info("memberId={}의 projectId={} 본문 조회 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);
        final ProjectBodyResponse projectBodyResponse = projectService.getProjectBody(projectId);
        return ResponseEntity.ok().body(projectBodyResponse);
    }

    @PostMapping("/{projectId}/body/edit")
    @MemberOnly
    public ResponseEntity<Void> updateProjectBody(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId,
            @RequestPart(value = "dto") @Valid final ProjectUpdateBodyRequest updateRequest,
            @RequestPart(value = "files", required = false) final List<MultipartFile> images
    ){
        log.info("memberId={}의 projectId={} 본문 수정 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);

        List<String> imageUrlList = null;
        if(!images.isEmpty()){
            imageUrlList = imageService.saveImages(images); // 프로젝트 사진에 변경 사항이 있음
        }

        projectService.updateProjectBody(projectId, updateRequest, imageUrlList);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/register")
    @MemberOnly
    public ResponseEntity<Void> registerProject(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId,
            @RequestPart(value = "dto") @Valid final ProjectUpdateBodyRequest updateRequest,
            @RequestPart(value = "files", required = false) final List<MultipartFile> images
    ){
        log.info("memberId={}의 projectId={} 등록 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);

        List<String> imageUrlList = null;
        if(images != null || !images.isEmpty()){
            imageUrlList = imageService.saveImages(images);
        }

        projectService.updateProjectBody(projectId, updateRequest, imageUrlList);
        projectService.registerProject(projectId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}")
    @MemberOnly
    public ResponseEntity<Void> deleteProject(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId
    ){
        log.info("memberId={}의 projectId={} 삭제 요청이 들어왔습니다.", accessor.getMemberId(), projectId);
        projectService.validateProjectByMember(accessor.getMemberId(), projectId);
        projectService.delete(projectId);
        return ResponseEntity.noContent().build();
    }

}
