package trackers.demo.like.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import trackers.demo.like.domain.Likes;
import trackers.demo.like.domain.repository.CustomLikeRepository;
import trackers.demo.like.dto.LikeElement;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CustomLikeRepositoryImpl implements CustomLikeRepository {

    // todo : 순수 SQL 문 -> QueryDsl 적용

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<LikeElement> likeElementRowMapper = (rs, rowNum) ->
            new LikeElement(
                    rs.getLong("projectId"),
                    rs.getLong("likeCount"),
                    parseMemberIds(rs.getString("memberIds")));

    private static Set<Long> parseMemberIds(final String memberIds) {
        if(!StringUtils.hasText(memberIds)){
            return Collections.emptySet();
        }

        // memberIds 문자열을 파싱하여 Set 형태로 반환
        final String[] idArray = memberIds.strip().split(",");
        return Arrays.stream(idArray)
                .filter(StringUtils::hasText)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public void saveAll(List<Likes> likes) {
        final String sql = """
                INSERT INTO likes (project_id, member_id)
                VALUES (:projectId, :memberId)
                """;
        namedParameterJdbcTemplate.batchUpdate(sql, getLikesToSQLParameterSources(likes));

    }

    private MapSqlParameterSource[] getLikesToSQLParameterSources(final List<Likes> likes) {
        return likes.stream()
                .map(this::getLikeToSQLParameterSources)
                .toArray(MapSqlParameterSource[]::new);
    }

    private MapSqlParameterSource getLikeToSQLParameterSources(final Likes likes) {
        return new MapSqlParameterSource()
                .addValue("projectId", likes.getProjectId())
                .addValue("memberId", likes.getMemberId());
    }

    @Override
    public Optional<LikeElement> findLikesElementByProjectId(final Long projectId) {
        final String sql = """
                SELECT l.project_id AS projectId, COUNT(l.id) AS likeCount, GROUP_CONCAT(l.member_id) AS memberIds
                FROM likes l
                WHERE l.project_id = :projectId
                GROUP BY l.project_id
                """;

        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("projectId", projectId);

        // SQL 쿼리를 실행하고 결과를 likeElementRowMapper를 통해 LikeElement 객체로 매핑
        final List<LikeElement> results = namedParameterJdbcTemplate.query(sql, parameters, likeElementRowMapper);
        if(results.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(results.get(0));
    }

    @Override
    public List<LikeElement> findLikeElementByProjectIds(final List<Long> projectIds) {
        final String sql = """
                SELECT l.project_id AS projectId, COUNT(l.id) AS likeCount, GROUP_CONCAT(l.member_id) AS memberIds
                FROM likes l
                WHERE l.project_id IN (:projectIds)
                GROUP BY l.project_id
                """;
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("projectIds", projectIds);
        return namedParameterJdbcTemplate.query(sql, parameters, likeElementRowMapper);
    }
}
