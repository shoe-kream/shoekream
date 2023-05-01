package com.shoekream.common.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import static com.shoekream.common.util.constants.DataSourceConstants.MASTER;
import static com.shoekream.common.util.constants.DataSourceConstants.SLAVE;
import static org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return isCurrentTransactionReadOnly() ? SLAVE : MASTER;
    }
}