package com.example.backendproject.config.datasource;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.sc5.datasource")
@Validated
public class HikariSc5DbConfig {
    @NotEmpty(message = "Missing database connection username")
    private String username;
    @NotEmpty(message = "Missing database connection password")
    private String password;
    @NotEmpty(message = "Missing database connection url")
    private String jdbcUrl;
    @NotNull(message = "Missing hikari config")
    private HikariConfig hikari;

    @Getter
    @Setter
    public static class HikariConfig {
        @Value("${spring.sc5.datasource.hikari.minimumIdle}")
        private Integer minimumIdle;
        @Value("${spring.sc5.datasource.hikari.maximumpoolsize}")
        private Integer maximumPoolSize ;
        private Integer idleTimeout = 30000;
        private Integer maxLifetime = 2000000;
        private Integer connectionTimeout = 30000;
        private String poolName = "HikariPoolSc5";
    }
}

