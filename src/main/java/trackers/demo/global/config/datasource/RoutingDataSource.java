package trackers.demo.global.config.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static trackers.demo.global.config.datasource.DataSourceType.REPLICA;
import static trackers.demo.global.config.datasource.DataSourceType.SOURCE;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        final String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        final boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if(isReadOnly){
            log.info(currentTransactionName + " Transaction:" + "Replica 서버로 요청합니다.");
            return REPLICA;
        }

        log.info(currentTransactionName + " Transaction:" + "Source 서버로 요청합니다.");
        return SOURCE;
    }
}
