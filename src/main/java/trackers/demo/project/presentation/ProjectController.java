package trackers.demo.project.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.dto.request.ProjectCreateSecondRequest;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ImageService imageService;

    @PostMapping("/first")
    @MemberOnly
    public ResponseEntity<Void> createProject(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final ProjectCreateFirstRequest createRequest,
            @RequestPart(value = "file") final MultipartFile mainImage
            ){
        // 원자성 문제가 생길 수 있음 -> try-catch 문으로 해결
        final String imageUrl = imageService.saveImage(mainImage);
        projectService.saveProjectFirst(accessor.getMemberId(), createRequest ,imageUrl);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/second")
    @MemberOnly
    public ResponseEntity<Void> createProjectBody(
            @Auth final Accessor accessor,
            @RequestPart(value = "dto") @Valid final ProjectCreateSecondRequest createRequest,
            @RequestPart(value = "files") final List<MultipartFile> images
            ){
        final List<String> imageUrlList = imageService.saveImages(images);
        final Long projectId =  projectService.saveProjectSecond(accessor.getMemberId(), createRequest, imageUrlList);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }


}
