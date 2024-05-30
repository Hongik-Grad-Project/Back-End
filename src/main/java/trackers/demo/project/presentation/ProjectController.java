package trackers.demo.project.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.service.ImageService;
import trackers.demo.project.service.ProjectService;

import java.net.URI;

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
            @RequestPart(value = "dto") final ProjectCreateFirstRequest projectCreateFirstRequest,
            @RequestPart(value = "file") final MultipartFile mainImage
            ){
        final String imageURL = imageService.save(mainImage);
        final Long projectId = projectService.save(
                accessor.getMemberId(),
                projectCreateFirstRequest.getProjectTitle(),
                projectCreateFirstRequest.getSubject(),
                projectCreateFirstRequest.getTarget(),
                imageURL);
        return ResponseEntity.created(URI.create("/project/" + projectId)).build();
    }


}
