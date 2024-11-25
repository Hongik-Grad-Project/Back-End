package trackers.demo.gallery.infrastructure.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import trackers.demo.project.domain.Tag;

import java.util.List;

import static trackers.demo.project.domain.QProjectTag.projectTag;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QuerydslProjectTagRepository {

    private final JPAQueryFactory queryFactory;

    public List<Tag> findPopularTagsByCount(final int count) {
        return queryFactory.select(projectTag.tag)
                .from(projectTag)
                .groupBy(projectTag.tag)
                .orderBy(projectTag.tag.count().desc())
                .limit(count)
                .fetch();
    }
}
