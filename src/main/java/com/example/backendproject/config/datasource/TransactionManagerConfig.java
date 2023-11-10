package com.example.backendproject.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

// Cau hinh TransactionManager quan ly viec rollback nhieu DB, khi them DB moi can them vao chainedTransactionManager nay
@Configuration
public class TransactionManagerConfig {

    @Bean
    @Primary
    public ChainedTransactionManager chainedTransactionManager (
            @Qualifier("sc5TransactionManager") PlatformTransactionManager sc5TransactionManager) {
        return new ChainedTransactionManager(sc5TransactionManager);
    }
}
