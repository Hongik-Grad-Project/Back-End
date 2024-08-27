package trackers.demo.gallery.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.auth.Auth;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.gallery.dto.request.ReadProjectTagCondition;
import trackers.demo.gallery.service.GalleryService;
import trackers.demo.gallery.configuration.DescendingSort;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;
import trackers.demo.gallery.dto.response.ProjectDetailResponse;
import trackers.demo.gallery.dto.response.ProjectResponse;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gallery")
@Slf4j
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjectsByCondition(
            @Auth final Accessor accessor,
            @DescendingSort final Pageable pageable,    // 정렬 조건
            final ReadProjectFilterCondition readProjectFilterCondition     // 필터 조건
    ){
        log.info("프로젝트 갤러리 조회 요청이 들어왔습니다.");
        final Page<ProjectResponse> projectResponses = galleryService.getAllProjectsByCondition(
                accessor,
                pageable,
                readProjectFilterCondition
        );
        return ResponseEntity.ok().body(projectResponses);
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<Page<ProjectResponse>> getAllByProjectsByKeyword(
            @Auth final Accessor accessor,
            @DescendingSort final Pageable pageable,
            final ReadProjectSearchCondition readProjectSearchCondition
    ){
        log.info("프로젝트 갤러리 검색 요청이 들어왔습니다. (by Keyword)");
        final Page<ProjectResponse> projectResponses = galleryService.getAllProjectsByKeyword(
                accessor,
                pageable,
                readProjectSearchCondition
        );
        return ResponseEntity.ok().body(projectResponses);
    }

    @GetMapping("/search/tag")
    public ResponseEntity<Page<ProjectResponse>> getAllProjectsByTags(
            @Auth final Accessor accessor,
            @DescendingSort final Pageable pageable,
            final ReadProjectTagCondition readProjectTagCondition
    ){
        log.info("프로젝트 갤러리 검색 요청이 들어왔습니다. (by Tag)");
        final Page<ProjectResponse> projectResponses = galleryService.getAllProjectsByTag(
                accessor,
                pageable,
                readProjectTagCondition
        );
        return ResponseEntity.ok().body(projectResponses);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProject(
            @Auth final Accessor accessor,
            @PathVariable final Long projectId
    ) {
        log.info("프로젝트 갤러리 단일 프로젝트 상세 조회 요청이 들어왔습니다.");
        galleryService.validateProjectByProjectId(projectId);
        final ProjectDetailResponse projectDetailResponse = galleryService.getProjectDetail(
                accessor,
                projectId
        );
        return ResponseEntity.ok().body(projectDetailResponse);
    }

}
