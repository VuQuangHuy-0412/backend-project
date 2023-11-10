package com.example.backendproject.config.datasource;

import com.example.backendproject.config.constant.PersistenceUnitName;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "adminEntityManager",
        transactionManagerRef = "adminTransactionManager",
        basePackages = {"com.example.backendproject.repository"}
)
public class Sc5DbConfig {
    private final HikariSc5DbConfig hikariSc5DbConfig;
    private static final String SUFFIX_POOL_NAME = "_sc5";

    public Sc5DbConfig(HikariSc5DbConfig hikariSc5DbConfig) {
        this.hikariSc5DbConfig = hikariSc5DbConfig;
    }

    @Bean(name = "sc5DataSource")
    @Primary
    public HikariDataSource sc5HikariDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(hikariSc5DbConfig.getUsername());
        hikariDataSource.setPassword(hikariSc5DbConfig.getPassword());
        hikariDataSource.setJdbcUrl(hikariSc5DbConfig.getJdbcUrl());
        hikariDataSource.setMinimumIdle(hikariSc5DbConfig.getHikari().getMinimumIdle());
        hikariDataSource.setMaximumPoolSize(hikariSc5DbConfig.getHikari().getMaximumPoolSize());
        hikariDataSource.setIdleTimeout(hikariSc5DbConfig.getHikari().getIdleTimeout());
        hikariDataSource.setMaxLifetime(hikariSc5DbConfig.getHikari().getMaxLifetime());
        hikariDataSource.setConnectionTimeout(hikariSc5DbConfig.getHikari().getConnectionTimeout());
        hikariDataSource.setPoolName(hikariSc5DbConfig.getHikari().getPoolName() + SUFFIX_POOL_NAME);
        return hikariDataSource;
    }

    @Bean(name = "sc5EntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean sc5EntityManager(@Qualifier("sc5DataSource") HikariDataSource sc5HikariDataSource) {
        return new LocalContainerEntityManagerFactoryBean() {{
            setDataSource(sc5HikariDataSource);
            Properties properties = new Properties();
            properties.put("hibernate.session_factory.statement_inspector", "com.example.backendproject.config.commonlib.SqlCommentStatementInspector");
            setJpaProperties(properties);
            setPersistenceProviderClass(HibernatePersistenceProvider.class);
            setPersistenceUnitName(PersistenceUnitName.SC5);
            setPackagesToScan("vn.ghtk.ewallet.admin.entity");
        }};
    }

    @Bean(name = "sc5TransactionManager")
    public PlatformTransactionManager sc5TransactionManager(@Qualifier("sc5EntityManager") EntityManagerFactory sc5EntityManager) {
        return new JpaTransactionManager(sc5EntityManager);
    }
}