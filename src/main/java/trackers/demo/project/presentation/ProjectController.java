package trackers.demo.project.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.project.dto.request.ProjectCreateFirstRequest;
import trackers.demo.project.service.ProjectService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/first")
    @MemberOnly
    public ResponseEntity<Void> createProject(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProjectCreateFirstRequest projectCreateFirstRequest
            ){
        final Long projectId = projectService.save(accessor.getMemberId(), projectCreateFirstRequest);
        return ResponseEntity.created(URI.create("/project/" + projectId)).build();
    }
}
