package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.model.sc5.ClassSearchRequest;
import com.example.backendproject.repository.sc5.custom.ClassRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ClassRepositoryCustomImpl implements ClassRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ClassRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Class> searchClassByFilter(ClassSearchRequest request) {
        String sql = "select c.* from class c where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = buildWhereSqlCommand(params, request);

        sql += where + " order by c.id desc limit :offset, :limit ";
        params.addValue("offset", request.getPage() * request.getPageSize());
        params.addValue("limit", request.getPageSize());

        List<Class> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Class.class));
        } catch (Exception exception) {
            log.error("Query search class failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }

    private String buildWhereSqlCommand(MapSqlParameterSource params, ClassSearchRequest request) {
        String where = "";
        if (request.getId() != null) {
            where += " and c.id = :id ";
            params.addValue("id", request.getId());
        }

        return where;
    }
}
