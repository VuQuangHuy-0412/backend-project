package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.Dataset;
import com.example.backendproject.model.sc5.DatasetSearchRequest;
import com.example.backendproject.repository.sc5.custom.DatasetRepositoryCustom;
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
public class DatasetRepositoryCustomImpl implements DatasetRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DatasetRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Dataset> searchDatasetByFilter(DatasetSearchRequest request) {
        String sql = "select t.* from dataset t where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = buildWhereSqlCommand(params, request);

        sql += where + " order by t.id desc ";

        List<Dataset> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Dataset.class));
        } catch (Exception exception) {
            log.error("Query search dataset failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }

    private String buildWhereSqlCommand(MapSqlParameterSource params, DatasetSearchRequest request) {
        String where = "";
        if (request.getId() != null) {
            where += " and t.id = :id ";
            params.addValue("id", request.getId());
        }

        if (!StringUtils.isBlank(request.getName())) {
            where += " and t.name like :name ";
            params.addValue("name", "%" + request.getName() + "%");
        }

        return where;
    }
}
