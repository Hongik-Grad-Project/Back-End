package trackers.demo.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import trackers.demo.global.config.datasource.RoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;

import static trackers.demo.global.config.datasource.DataSourceType.REPLICA;
import static trackers.demo.global.config.datasource.DataSourceType.SOURCE;

@Profile({"dev" ,"prod"})
@Configuration
public class DataSourceConfig {

    private static final String SOURCE_SERVER = "SOURCE";
    private static final String REPLICA_SERVER = "REPLICA";

    @Bean
    @Qualifier(SOURCE_SERVER)
    @ConfigurationProperties(prefix = "spring.datasource.source")
    public DataSource sourceDataSource() { return DataSourceBuilder.create().build(); }

    @Bean
    @Qualifier(REPLICA_SERVER)
    @ConfigurationProperties(prefix = "spring.datasource.replica")
    public DataSource replicaDataSource() { return DataSourceBuilder.create().build(); }

    @Bean
    public DataSource routingDataSource(
            @Qualifier(SOURCE_SERVER) final DataSource sourceDataSource,
            @Qualifier(REPLICA_SERVER) final DataSource replicaDataSource
    ) {
        final RoutingDataSource routingDataSource = new RoutingDataSource();

        final HashMap<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(SOURCE, sourceDataSource);
        dataSourceMap.put(REPLICA, replicaDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(sourceDataSource);

        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        final DataSource determinedDataSource = routingDataSource(sourceDataSource(), replicaDataSource());
        return new LazyConnectionDataSourceProxy(determinedDataSource);
    }
}
