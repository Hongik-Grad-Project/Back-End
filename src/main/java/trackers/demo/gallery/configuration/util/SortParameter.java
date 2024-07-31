package trackers.demo.gallery.configuration.util;

import trackers.demo.global.exception.UnsupportedSortParameterException;

import java.util.Arrays;

import static trackers.demo.global.exception.ExceptionCode.*;

public enum SortParameter {

    ID("new", ProjectSortConditionConsts.ID),
    LIKE_COUNT("likeCount", ProjectSortConditionConsts.LIKE_COUNT),
    RECENT_TIME("recentTime", ProjectSortConditionConsts.RECENT_TIME),
    CLOSING_TIME("closingTIme", ProjectSortConditionConsts.CLOSING_TIME);

    private final String sortParameter;     // 정렬 파라미터
    private final String sortCondition;     // 정렬 조건

    SortParameter(final String sortParameter, final String sortCondition){
        this.sortParameter = sortParameter;
        this.sortCondition = sortCondition;
    }

    public static String findSortProperty(final String targetSortParameter){
        if(isDefaultSortCondition(targetSortParameter)){
            return SortParameter.ID.sortCondition;
        }
        return Arrays.stream(SortParameter.values())
                .filter(sortParameter -> verifyEquality(targetSortParameter, sortParameter))
                .map(sortParameter -> sortParameter.sortCondition)
                .findAny()
                .orElseThrow(() -> new UnsupportedSortParameterException(UNSUPPORTED_SORT_PARAMETER));
    }

    private static boolean isDefaultSortCondition(final String targetSortParameter) {
        // null 검사 || 빈 문자열 검사 || 공백 문자열 검사
        return targetSortParameter == null || targetSortParameter.isEmpty() || targetSortParameter.isBlank();
    }

    private static boolean verifyEquality(
            final String targetSortParameter,
            final SortParameter sortParameter) {
        return sortParameter.sortParameter.equalsIgnoreCase(targetSortParameter);
    }


}
