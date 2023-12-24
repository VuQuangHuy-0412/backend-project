package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.ConstraintSearchRequest;
import com.example.backendproject.model.sc5.RequiredConstraint;
import com.example.backendproject.repository.sc5.custom.RequiredConstraintRepositoryCustom;
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
public class RequiredConstraintRepositoryCustomImpl implements RequiredConstraintRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RequiredConstraintRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<RequiredConstraint> searchRequiredConstraintByFilter(ConstraintSearchRequest request) {
        String sql = "select rc.* from required_constraint rc where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (request.getStatus() != null) {
            sql += " and rc.status = :status ";
            params.addValue("status", request.getStatus());
        }

        sql += " order by rc.created_at desc ";

        List<RequiredConstraint> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(RequiredConstraint.class));
        } catch (Exception exception) {
            log.error("Query search required constraint failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }
}
