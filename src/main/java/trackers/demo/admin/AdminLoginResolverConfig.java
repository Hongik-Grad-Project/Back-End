package trackers.demo.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdminLoginResolverConfig implements WebMvcConfigurer {

    private final AdminLoginArgumentResolver adminLoginArgumentResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers){
        resolvers.add(adminLoginArgumentResolver);
    }
}
