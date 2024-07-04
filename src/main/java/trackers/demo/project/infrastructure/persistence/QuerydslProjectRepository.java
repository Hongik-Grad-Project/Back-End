package trackers.demo.project.infrastructure.persistence;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import trackers.demo.global.common.helper.QuerydslSliceHelper;
import trackers.demo.project.configuration.util.ProjectSortConditionConsts;
import trackers.demo.project.domain.Project;
import trackers.demo.project.dto.request.ReadProjectSearchCondition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.domain.Sort.*;
import static trackers.demo.project.domain.QProject.project;


@Repository
@RequiredArgsConstructor
public class QuerydslProjectRepository {

    private static final long SLICE_OFFSET = 1L;
    private static final int HIGH_PRIORITY = 2;
    private static final int LOW_PRIORITY = 1;

    private final JPAQueryFactory queryFactory;

    public Slice<Project> findProjectsAllByCondition(
            final ReadProjectSearchCondition readProjectSearchCondition,
            final Pageable pageable
    ){
        // 정렬 조건 검색
        final List<OrderSpecifier<?>> orderSpecifiers = calculateOrderSpecifiers(pageable);
        // 검색 조건 검색
        final List<BooleanExpression> booleanExpressions = calculateBooleanExpressions(readProjectSearchCondition);
        // 검색 조건과 정렬 검색을 사용하여 프로젝트 ID 목록 검색
        final List<Long> findProjectIds = findProjectIds(booleanExpressions, orderSpecifiers, pageable);
        // 검색된 ID를 사용하여 실제 프로젝트 항목 검색
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
            return project.likes.desc();
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

    private List<BooleanExpression> calculateBooleanExpressions(final ReadProjectSearchCondition searchCondition) {
        final List<BooleanExpression> booleanExpressions = new ArrayList<>();

        booleanExpressions.add(project.deleted.isFalse());

        final BooleanExpression titleBooleanExpression = covertTitleSearchCondition(searchCondition);

        if(titleBooleanExpression != null){
            booleanExpressions.add(titleBooleanExpression);
        }

        return booleanExpressions;
    }

    private List<Long> findProjectIds(
            final List<BooleanExpression> booleanExpressions,
            final List<OrderSpecifier<?>> orderSpecifiers,
            final Pageable pageable) {
        return queryFactory.select(project.id)
                .from(project)
                .where(booleanExpressions.toArray(BooleanExpression[]::new))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize() + SLICE_OFFSET)
                .offset(pageable.getOffset())
                .fetch();
    }

    private BooleanExpression covertTitleSearchCondition(final ReadProjectSearchCondition readProjectSearchCondition) {
        final String titleSearchCondition = readProjectSearchCondition.getTitle();

        if(titleSearchCondition == null){
            return null;
        }
        return project.projectTitle.like("%" + titleSearchCondition + "%");
    }

    private List<Project> findProjectsByIdsAndOrderSpecifiers(
            final List<Long> targetIds,
            final List<OrderSpecifier<?>> orderSpecifiers) {
        return queryFactory.selectFrom(project)
                .where(project.id.in(targetIds.toArray(Long[]::new)))
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .fetch();
    }



}
