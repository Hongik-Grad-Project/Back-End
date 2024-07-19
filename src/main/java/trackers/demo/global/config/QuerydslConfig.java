package trackers.demo.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class QuerydslConfig {

    private final EntityManager em;

    // EntityManager를 사용하여 쿼리를 생성하는 데 사용됨
    @Bean
    public JPAQueryFactory queryFactory() { return new JPAQueryFactory(em); }
}
