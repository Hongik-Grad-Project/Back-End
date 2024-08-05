package trackers.demo.project.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.project.dto.request.ProjectCreateOutlineRequest;
import trackers.demo.project.dto.request.ProjectCreateBodyRequest;
import trackers.demo.project.dto.request.ProjectUpdateOutlineRequest;
import trackers.demo.project.dto.response.ProjectOutlineResponse;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.net.URI;
import java.util.List;

import static trackers.demo.project.domain.type.CompletedStatusType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
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
        // 원자성 문제가 생길 수 있음 -> try-catch 문으로 해결 예정
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
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);
        final List<String> imageUrlList = imageService.saveImages(images);
        projectService.saveProjectBody(accessor.getMemberId(), projectId, createRequest, imageUrlList);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }

    @GetMapping("{projectId}/outline")
    @MemberOnly
    public ResponseEntity<ProjectOutlineResponse> getProjectOutline(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId
    ){
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);
        final ProjectOutlineResponse projectOutlineResponse = projectService.getProjectOutline(projectId);
        return ResponseEntity.ok().body(projectOutlineResponse);
    }

    @PutMapping("{projectId}/outline")
    @MemberOnly
    public ResponseEntity<Void> updateProjectOutline(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId,
            @RequestPart(value = "dto") @Valid final ProjectUpdateOutlineRequest updateRequest,
            @RequestPart(value = "file") final MultipartFile mainImage
    ){
        projectService.validateProjectByMemberAndProjectStatus(accessor.getMemberId(), projectId, NOT_COMPLETED);

        String imageUrl = null;
        if(mainImage != null && !mainImage.isEmpty()){    // 프로젝트 대표사진에 수정이 있음
            imageUrl = imageService.saveImage(mainImage);
        }

        projectService.updateProjectOutline(projectId, updateRequest, imageUrl);

        return ResponseEntity.noContent().build();
    }

    // todo: 프로젝트 본문 반환 (API: GET {projectId}/body)

    // todo: 프로젝트 본문 수정 (API: PUT {projectId}/body)

    // todo: 프로젝트 등록 (API: POST {projectId}/body/register)

    // todo: 프로젝트 삭제 (API: DELETE {projectId})

}
