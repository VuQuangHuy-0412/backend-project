package com.example.backendproject.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

    @Bean
    NamedParameterJdbcTemplate sc5JdbcTemplate(@Qualifier("sc5DataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
