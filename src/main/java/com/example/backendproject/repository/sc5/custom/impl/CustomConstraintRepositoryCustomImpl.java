package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.ConstraintSearchRequest;
import com.example.backendproject.model.sc5.CustomConstraint;
import com.example.backendproject.repository.sc5.custom.CustomConstraintRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CustomConstraintRepositoryCustomImpl implements CustomConstraintRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CustomConstraintRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<CustomConstraint> searchCustomConstraintByFilter(ConstraintSearchRequest request) {
        String sql = "select cc.* from custom_constraint cc where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (request.getStatus() != null) {
            sql += " and cc.status = :status ";
            params.addValue("status", request.getStatus());
        }

        if (request.getDataset() != null) {
            sql += " and cc.dataset = :dataset ";
            params.addValue("dataset", request.getDataset());
        }

        sql += " order by cc.created_at desc ";

        List<CustomConstraint> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(CustomConstraint.class));
        } catch (Exception exception) {
            log.error("Query search custom constraint failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }
}
