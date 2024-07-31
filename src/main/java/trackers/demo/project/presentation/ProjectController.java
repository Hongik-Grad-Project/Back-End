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

    @PostMapping("{projectId}/body/save")
    @MemberOnly
    public ResponseEntity<Void> saveProjectBody(
            @Auth final Accessor accessor,
            @PathVariable(name = "projectId") final Long projectId,
            @RequestPart(value = "dto") @Valid final ProjectCreateBodyRequest createRequest,
            @RequestPart(value = "files") final List<MultipartFile> images
            ){
        projectService.validateProjectByMemberAndProjectId(accessor.getMemberId(), projectId, NOT_COMPLETED);
        final List<String> imageUrlList = imageService.saveImages(images);
        projectService.saveProjectBody(accessor.getMemberId(), projectId, createRequest, imageUrlList);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }


    // todo: 프로젝트 개요 반환 (API: GET {projectId}/outline)

    // todo: 프로젝트 개요 수정 (API: POST {projectId}/outline/edit)

    // todo: 프로젝트 본문 반환 (API: GET {projectId}/body)

    // todo: 프로젝트 본문 수정 (API: POST {projectId}/body/edit)

    // todo: 프로젝트 등록 (API: POST {projectId}/body/register)

}
