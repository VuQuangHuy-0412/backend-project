package com.example.backendproject.repository.custom.impl;

import com.example.backendproject.entity.FileEntity;
import com.example.backendproject.model.FileExportFilter;
import com.example.backendproject.repository.custom.FileRepositoryCustom;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

public class FileRepositoryCustomImpl implements FileRepositoryCustom {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FileRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<FileEntity> findAllByFilter(FileExportFilter filter) {
        String sql = "select f.* from file f where 1 = 1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.isNotEmpty(filter.getId())) {
            sql += "and f.id = :id ";
            params.addValue("id", filter.getId());
        }
        sql += " order by f.id desc limit :offset, :limit ";
        params.addValue("offset", filter.getPage() * filter.getPageSize());
        params.addValue("limit", filter.getPageSize());
        List<FileEntity> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(FileEntity.class));
        } catch (Exception exception) {
            result = Collections.emptyList();
        }
        return result;
    }

    public Long findTotalCountByFilter(FileExportFilter filter) {
        String sql = "select count(*) from file f where 1 = 1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.isNotEmpty(filter.getId())) {
            sql += "and f.id = :id ";
            params.addValue("id", filter.getId());
        }
        Long result;
        try {
            result = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        } catch (Exception exception) {
            result = 0L;
        }
        return result;
    }
}
