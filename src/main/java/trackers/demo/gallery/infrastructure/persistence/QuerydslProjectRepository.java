package trackers.demo.gallery.infrastructure.persistence;

import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Sort.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import trackers.demo.gallery.dto.request.ReadProjectTagCondition;
import trackers.demo.global.common.helper.QuerydslSliceHelper;
import trackers.demo.like.domain.QLikes;
import trackers.demo.gallery.configuration.util.ProjectSortConditionConsts;
import trackers.demo.project.domain.Project;
import trackers.demo.gallery.dto.request.ReadProjectFilterCondition;
import trackers.demo.gallery.dto.request.ReadProjectSearchCondition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trackers.demo.like.domain.QLikes.*;
import static trackers.demo.project.domain.QProject.project;
import static trackers.demo.project.domain.QProjectTag.projectTag;
import static trackers.demo.project.domain.QProjectTarget.projectTarget;
import static trackers.demo.project.domain.QTag.tag;
import static trackers.demo.project.domain.QTarget.target;
import static trackers.demo.project.domain.type.CompletedStatusType.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QuerydslProjectRepository {

    private static final long SLICE_OFFSET = 1L;
    private static final int HIGH_PRIORITY = 2;
    private static final int LOW_PRIORITY = 1;

    private final JPAQueryFactory queryFactory;

    public Slice<Project> findProjectsAllByCondition(
            final ReadProjectFilterCondition readProjectFilterCondition,
            final Pageable pageable
    ){
        log.info("정렬 조건 검색");
        final List<OrderSpecifier<?>> orderSpecifiers = calculateOrderSpecifiers(pageable);
        log.info("필터 조건 검색");
        final List<BooleanExpression> filterBooleanExpressions = calculateFilterBooleanExpressions(readProjectFilterCondition);
        log.info("검색 조건을 통해 프로젝트 ID 리스트 검색");
        final List<Long> findProjectIds = findProjectIdsByConditions(filterBooleanExpressions, orderSpecifiers, pageable);
        log.info("프로젝트 ID 리스트를 통해 프로젝트 반환");
        final List<Project> findProjects = findProjectsByIdsAndOrderSpecifiers(findProjectIds, orderSpecifiers);
        return QuerydslSliceHelper.toSlice(findProjects, pageable);
    }

    public Slice<Project> findProjectsAllByKeyword(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final Pageable pageable) {
        log.info("정렬 조건 검색");
        final List<OrderSpecifier<?>> orderSpecifiers = calculateOrderSpecifiers(pageable);
        log.info("키워드 조건 검색");
        final List<BooleanExpression> searchBooleanExpressions = calculateSearchBooleanExpressions(readProjectSearchCondition);
        log.info("검색 조건을 통해 프로젝트 ID 리스트 검색");
        final List<Long> findProjectIds = findProjectIdsByKeyword(searchBooleanExpressions, orderSpecifiers, pageable);
        log.info("프로젝트 ID 리스트를 통해 프로젝트 반환");
        final List<Project> findProjects = findProjectsByIdsAndOrderSpecifiers(findProjectIds, orderSpecifiers);
        return QuerydslSliceHelper.toSlice(findProjects, pageable);
    }

    public Slice<Project> findProjectsAllByTags(
            final ReadProjectTagCondition readProjectTagCondition,
            final Pageable pageable) {
        log.info("정렬 조건 검색");
        final List<OrderSpecifier<?>> orderSpecifiers = calculateOrderSpecifiers(pageable);
        log.info("태그 조건 검색");
        final List<BooleanExpression> tagBooleanExpressions = calculateTagBooleanExpressions(readProjectTagCondition);
        log.info("태그 조건을 통해 프로젝트 ID 리스트 검색");
        final List<Long> findProjectIds = findProjectIdsByTags(tagBooleanExpressions, orderSpecifiers, pageable);
        log.info("프로젝트 ID 리스트를 통해 프로젝트 반환");
        final List<Project> findProjects = findProjectsByIdsAndOrderSpecifiers(findProjectIds, orderSpecifiers);
        return QuerydslSliceHelper.toSlice(findProjects, pageable);
    }

    private List<OrderSpecifier<?>> calculateOrderSpecifiers(final Pageable pageable){
        final List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 마감 시간 기준 정렬 추가
        orderSpecifiers.add(closingTimeOrderSpecifier());
        // 페이징 조건에 따른 추가 정렬 조건 계산
        orderSpecifiers.addAll(processOrderSpecifiers(pageable));
        // 프로젝트 ID 내림차순 정렬 추가
        orderSpecifiers.add(project.id.desc());

        return orderSpecifiers;
    }

    private List<OrderSpecifier<?>> processOrderSpecifiers(final Pageable pageable) {
        final List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        final Sort sort = pageable.getSort();

        for(final Order order: sort){
            if(ProjectSortConditionConsts.ID.equals(order.getProperty())){
                return Collections.emptyList();
            }
            orderSpecifiers.add(processOrderSpecifierByCondition(order));
        }

        return orderSpecifiers;
    }

    private OrderSpecifier<?> processOrderSpecifierByCondition(final Order order) {
        if(ProjectSortConditionConsts.LIKE_COUNT.equals(order.getProperty())){
            QLikes subLikes = new QLikes("subLikes");
            return Expressions.asNumber(
                    queryFactory
                            .select(subLikes.count())
                            .from(subLikes)
                            .where(subLikes.projectId.eq(project.id))
            ).desc();
        }
        if(ProjectSortConditionConsts.RECENT_TIME.equals(order.getProperty())) {
            return project.createdAt.asc();
        }
        if(ProjectSortConditionConsts.CLOSING_TIME.equals(order.getProperty())){
            return project.endDate.asc();
        }
        return null;
    }


    private OrderSpecifier<Integer> closingTimeOrderSpecifier(){
        final LocalDate now = LocalDate.now();

        return new CaseBuilder()
                .when(project.endDate.after(now)).then(LOW_PRIORITY)
                .otherwise(HIGH_PRIORITY)
                .asc();
    }

    private List<BooleanExpression> calculateFilterBooleanExpressionsV1(final List<String> targets) {
        final List<BooleanExpression> booleanExpressions = new ArrayList<>();

        // 프로젝트 대상 필터
        if(targets != null ){
            List<Long> targetIds = queryFactory
                    .select(target.id)
                    .from(target)
                    .where(target.targetTitle.in(targets))
                    .fetch();

            if(!targetIds.isEmpty()){
                booleanExpressions.add(projectTarget.target.id.in(targetIds));
            } else {
                booleanExpressions.add(projectTarget.target.id.isNull());
            }
        }

        // 등록된 프로젝트 검색 필터
        booleanExpressions.add(project.completedStatus.eq(COMPLETED));
        return booleanExpressions;
    }

    private List<BooleanExpression> calculateFilterBooleanExpressions(final ReadProjectFilterCondition readProjectFilterCondition) {
        final List<BooleanExpression> booleanExpressions = new ArrayList<>();

//        // 모금 전, 모금 중 필터
//        boolean isDonated = readProjectFilterCondition.isDonated();
//        if(!isDonated){
//            booleanExpressions.add(project.donatedStatus.eq(DonatedStatusType.NOT_DONATED));
//        } else {
//            booleanExpressions.add(project.donatedStatus.eq(DonatedStatusType.DONATED));
//        }

        // 프로젝트 대상 필터
        if(readProjectFilterCondition.getTargets() != null ){
            List<String> targets = readProjectFilterCondition.getTargets();
            List<Long> targetIds = queryFactory
                    .select(target.id)
                    .from(target)
                    .where(target.targetTitle.in(targets))
                    .fetch();

            if(!targetIds.isEmpty()){
                booleanExpressions.add(projectTarget.target.id.in(targetIds));
            } else {
                booleanExpressions.add(projectTarget.target.id.isNull());
            }
        }

        // 등록된 프로젝트 검색 필터
        booleanExpressions.add(project.completedStatus.eq(COMPLETED));
        return booleanExpressions;
    }

    private List<BooleanExpression> calculateSearchBooleanExpressions(final ReadProjectSearchCondition searchCondition) {
        final List<BooleanExpression> booleanExpressions = new ArrayList<>();

        // 키워드 기반으로 검색
        final BooleanExpression keywordBooleanExpression = covertKeywordSearchCondition(searchCondition);

        if(keywordBooleanExpression != null){
            booleanExpressions.add(keywordBooleanExpression);
        }

        // 등록된 프로젝트 검색 필터
        booleanExpressions.add(project.completedStatus.eq(COMPLETED));
        return booleanExpressions;
    }

    private BooleanExpression covertKeywordSearchCondition(final ReadProjectSearchCondition readProjectSearchCondition) {
        final String keywordSearchCondition = readProjectSearchCondition.getKeyword();

        if(keywordSearchCondition == null){
            return null;
        }

        BooleanExpression titleCondition = project.projectTitle.like("%" + keywordSearchCondition + "%");
        BooleanExpression tagCondition = queryFactory
                .selectFrom(projectTag)
                .leftJoin(projectTag.tag, tag)
                .where(tag.tagTitle.like("%" + keywordSearchCondition + "%")
                        .and(projectTag.project.eq(project)))
                .exists();

        return titleCondition.or(tagCondition);
    }

    private List<BooleanExpression> calculateTagBooleanExpressions(final ReadProjectTagCondition tagCondition) {
        final List<BooleanExpression> booleanExpressions = new ArrayList<>();

        // 프로젝트 태그 필터
        if(tagCondition.getTags() != null ){
            List<String> tags = tagCondition.getTags();
            List<Long> tagIds = queryFactory
                    .select(tag.id)
                    .from(tag)
                    .where(tag.tagTitle.in(tags))
                    .fetch();

            if(!tagIds.isEmpty()){
                booleanExpressions.add(projectTag.tag.id.in(tagIds));
            } else {
                booleanExpressions.add(projectTag.tag.id.isNull());
            }
        }

        // 등록된 프로젝트 검색 필터
        booleanExpressions.add(project.completedStatus.eq(COMPLETED));
        return booleanExpressions;
    }

    private List<Long> findProjectIdsByConditions(
            final List<BooleanExpression> booleanFilterExpressions,
            final List<OrderSpecifier<?>> orderSpecifiers,
            final Pageable pageable) {
        return queryFactory.select(project.id)
                .from(project)
                .leftJoin(projectTarget).on(projectTarget.project.eq(project))
                .where(booleanFilterExpressions.toArray(BooleanExpression[]::new))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize() + SLICE_OFFSET)
                .offset(pageable.getOffset())
                .fetch();
    }

    private List<Long> findProjectIdsByKeyword(
            final List<BooleanExpression> booleanSearchExpressions,
            final List<OrderSpecifier<?>> orderSpecifiers,
            final Pageable pageable) {
        return queryFactory.select(project.id)
                .from(project)
                .where(booleanSearchExpressions.toArray(BooleanExpression[]::new))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize() + SLICE_OFFSET)
                .offset(pageable.getOffset())
                .fetch();
    }

    private List<Long> findProjectIdsByTags(
            final List<BooleanExpression> booleanTagsExpressions,
            final List<OrderSpecifier<?>> orderSpecifiers,
            final Pageable pageable) {
        return queryFactory.select(project.id)
                .from(project)
                .leftJoin(projectTag).on(projectTag.project.eq(project))
                .where(booleanTagsExpressions.toArray(BooleanExpression[]::new))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize() + SLICE_OFFSET)
                .offset(pageable.getOffset())
                .fetch();
    }

    private List<Project> findProjectsByIdsAndOrderSpecifiers(
            final List<Long> projectIds,
            final List<OrderSpecifier<?>> orderSpecifiers) {
        return queryFactory.selectFrom(project)
                .where(project.id.in(projectIds.toArray(Long[]::new)))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .fetch();
    }

    public List<Project> findLikedProjects(final Long memberId) {
        return queryFactory.select(project)
                .from(likes)
                .join(project).on(likes.projectId.eq(project.id))
                .where(likes.memberId.eq(memberId))
                .orderBy(likes.createdAt.desc())
                .fetch();
    }


    public List<Project> findLikedProjects(final Long memberId, final Pageable pageable) {
        return queryFactory.select(project)
                .from(likes)
                .join(project).on(likes.projectId.eq(project.id))
                .where(likes.memberId.eq(memberId))
                .orderBy(likes.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<Project> getMyRecentProjects(final Long memberId, final Pageable pageable) {

        List<Project> myProjects = queryFactory
                .selectFrom(project)
                .where(
                        project.member.id.eq(memberId)
                                .and(project.completedStatus.eq(NOT_COMPLETED))
                )
                .orderBy(project.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        if(myProjects.size() < pageable.getPageSize()){
            List<Project> completedProjects = queryFactory
                    .selectFrom(project)
                    .where(
                            project.member.id.eq(memberId)
                                    .and(project.completedStatus.eq(COMPLETED))
                    )
                    .orderBy(project.createdAt.desc())
                    .limit(pageable.getPageSize() - myProjects.size())
                    .fetch();

            myProjects.addAll(completedProjects);
        }

        return myProjects;
    }

}
