package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.Subject;
import com.example.backendproject.model.sc5.SubjectSearchRequest;
import com.example.backendproject.repository.sc5.custom.SubjectRepositoryCustom;
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
public class SubjectRepositoryCustomImpl implements SubjectRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SubjectRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Subject> searchSubjectByFilter(SubjectSearchRequest request) {
        String sql = "select t.* from subject t where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = buildWhereSqlCommand(params, request);

        sql += where + " order by t.id desc limit :offset, :limit ";
        params.addValue("offset", request.getPage() * request.getPageSize());
        params.addValue("limit", request.getPageSize());

        List<Subject> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Subject.class));
        } catch (Exception exception) {
            log.error("Query search subject failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }

    private String buildWhereSqlCommand(MapSqlParameterSource params, SubjectSearchRequest request) {
        String where = "";
        if (request.getId() != null) {
            where += " and t.id = :id ";
            params.addValue("id", request.getId());
        }

        if (!StringUtils.isBlank(request.getName())) {
            where += " and t.name like :name ";
            params.addValue("name", "%" + request.getName() + "%");
        }

        if (!StringUtils.isBlank(request.getCode())) {
            where += " and t.code = :code ";
            params.addValue("code", "%" + request.getCode() + "%");
        }

        if (request.getDataset() != null) {
            where += " and t.dataset = :dataset ";
            params.addValue("dataset", request.getDataset());
        }

        return where;
    }
}
