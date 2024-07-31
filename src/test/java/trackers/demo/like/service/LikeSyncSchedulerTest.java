package trackers.demo.like.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import trackers.demo.integration.service.RedisServiceIntegrationTest;
import trackers.demo.like.domain.Likes;
import trackers.demo.like.domain.repository.LikeRepository;
import trackers.demo.like.dto.request.LikeRequest;
import trackers.demo.project.service.ProjectService;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static trackers.demo.integration.IntegrationFixture.*;

public class LikeSyncSchedulerTest extends RedisServiceIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LikeSyncScheduler likeSyncScheduler;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LikeService likeService;

    private Long memberId;
    private Long projectId1;
    private Long projectId2;

    @BeforeEach
    void setUp(){
        // Redis 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 데이터 추가
        memberId = member.getId();

        Long projectId1 = projectService.saveProjectOutline(memberId, PROJECT_CREATE_FIRST_REQUEST, "프로젝트 대표 사진 URL");
        projectService.saveProjectBody(
                memberId,
                projectId1,
                PROJECT_CREATE_SECOND_REQUEST,
                List.of("프로젝트 이미지 URL", "프로젝트 이미지 URL"));

        Long projectId2 = projectService.saveProjectOutline(memberId, PROJECT_CREATE_FIRST_REQUEST, "프로젝트 대표 사진 URL");
        projectService.saveProjectBody(
                memberId,
                projectId2,
                PROJECT_CREATE_SECOND_REQUEST,
                List.of("프로젝트 이미지 URL", "프로젝트 이미지 URL"));

    }

    /**
     * 테스트 전 application-yml ddl-auto: update, sql.init.mode: never 로 변경
     * 테스트 후 init.sql이 실행되어 더미 데이터가 삭제됨
    * */
    @DisplayName("Redis에서 업데이트 된 좋아요 값을 DB에 저장한다. (기존에 저장되어 있는 ProjectId1을 삭제하고 ProjectId2 데이터만 남는다.")
    @Test
    void writeBackLikeCache(){
        // given
        final Likes likeProjectId1 = new Likes(projectId1, memberId);
        likeRepository.save(likeProjectId1);

        likeService.update(memberId, projectId1, new LikeRequest(false));
        likeService.update(memberId, projectId2, new LikeRequest(true));

        // when
        likeSyncScheduler.writeBackLikeCache();
        final List<Likes> likes = likeRepository.findAll();

        // then
        assertSoftly(softly -> {
            softly.assertThat(likes.size()).isEqualTo(1);
            softly.assertThat(likes.get(0)).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(new Likes(projectId2, memberId));
        });

    }


}
