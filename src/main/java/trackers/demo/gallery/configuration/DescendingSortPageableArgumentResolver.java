package trackers.demo.gallery.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import trackers.demo.gallery.configuration.util.ProjectSortConditionConsts;
import trackers.demo.gallery.configuration.util.SortParameter;

@Component
public class DescendingSortPageableArgumentResolver implements HandlerMethodArgumentResolver {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 8;

    // 파라미터 타입이 Pageable이고 DescendingSort 어노테이션이 붙어 있는 경우 true 반환
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(Pageable.class) &&
                parameter.hasParameterAnnotation(DescendingSort.class);
    }

    // Pageable 객체를 생성하여 반환
    @Override
    public Object resolveArgument(
            final MethodParameter ignoredParameter,
            final ModelAndViewContainer ignoredMavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory ignoredBinderFactory) {
        final int size = processSizeParameter(webRequest.getParameter("size"));
        final int page = processPageParameter(webRequest.getParameter("page"));
        final Sort sort = processSortParameter(webRequest.getParameter("sortType"));

        return PageRequest.of(page, size, sort);
    }

    private int processSizeParameter(final String sizeParameter) {
        if(sizeParameter == null){
            return DEFAULT_SIZE;
        }
        return Integer.parseInt(sizeParameter);
    }

    private int processPageParameter(final String pageParameter) {
        if(pageParameter == null){
            return DEFAULT_PAGE;
        }

        return Integer.parseInt(pageParameter) - 1;
    }

    private Sort processSortParameter(final String sortParameter) {
        if(ProjectSortConditionConsts.CLOSING_TIME.equals(sortParameter)){
            return Sort.by(Sort.Direction.ASC, SortParameter.findSortProperty(sortParameter));
        }

        return Sort.by(Sort.Direction.DESC, SortParameter.findSortProperty(sortParameter));
    }
}
