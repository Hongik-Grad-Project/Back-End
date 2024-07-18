package trackers.demo.project.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.project.configuration.DescendingSort;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.dto.request.ProjectCreateSecondRequest;
import trackers.demo.project.dto.request.ReadProjectFilterCondition;
import trackers.demo.project.dto.request.ReadProjectSearchCondition;
import trackers.demo.project.dto.response.ProjectDetailResponse;
import trackers.demo.project.dto.response.ProjectResponse;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static trackers.demo.project.domain.type.CompletedStatusType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ImageService imageService;

    @PostMapping("/first")
    @MemberOnly
    public ResponseEntity<Void> createProjectFirst(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final ProjectCreateFirstRequest createRequest,
            @RequestPart(value = "file") final MultipartFile mainImage
            ){
        // 원자성 문제가 생길 수 있음 -> try-catch 문으로 해결 예정
        final String imageUrl = imageService.saveImage(mainImage);
        projectService.saveProjectFirst(accessor.getMemberId(), createRequest ,imageUrl);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/second")
    @MemberOnly
    public ResponseEntity<Void> createProjectSecond(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final ProjectCreateSecondRequest createRequest,
            @RequestPart(value = "files") final List<MultipartFile> images
            ){
        projectService.validateProjectByMember(accessor.getMemberId(), NOT_COMPLETED);
        final List<String> imageUrlList = imageService.saveImages(images);
        final Long projectId =  projectService.saveProjectSecond(accessor.getMemberId(), createRequest, imageUrlList);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProject(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId
    ) {
        projectService.validateProjectByProjectId(projectId);
        final ProjectDetailResponse projectDetailResponse = projectService.getProjectDetail(
                accessor,
                projectId
        );
        return ResponseEntity.ok().body(projectDetailResponse);
    }

    @GetMapping("/gallery")
    public ResponseEntity<List<ProjectResponse>> getAllProjectsByCondition(
            @Auth final Accessor accessor,
            @DescendingSort final Pageable pageable,    // 정렬 조건
            final ReadProjectSearchCondition readProjectSearchCondition,     // 검색 조건
            final ReadProjectFilterCondition readProjectFilterCondition     // 필터 조건
            ){
        final List<ProjectResponse> projectResponses = projectService.getAllProjectsByCondition(
                accessor,
                pageable,
                readProjectSearchCondition,
                readProjectFilterCondition
        );
        return ResponseEntity.ok().body(projectResponses);
    }
}
