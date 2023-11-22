package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.StudentProject;
import com.example.backendproject.model.sc5.StudentProjectSearchRequest;
import com.example.backendproject.repository.sc5.custom.StudentProjectRepositoryCustom;
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
public class StudentProjectRepositoryCustomImpl implements StudentProjectRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StudentProjectRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<StudentProject> searchStudentProjectByFilter(StudentProjectSearchRequest request) {
        String sql = "select sp.* from student_project sp where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = buildWhereSqlCommand(params, request);

        sql += where + " order by sp.id desc limit :offset, :limit ";
        params.addValue("offset", request.getPage() * request.getPageSize());
        params.addValue("limit", request.getPageSize());

        List<StudentProject> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(StudentProject.class));
        } catch (Exception exception) {
            log.error("Query search student project failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }

    private String buildWhereSqlCommand(MapSqlParameterSource params, StudentProjectSearchRequest request) {
        String where = "";
        if (request.getId() != null) {
            where += " and sp.id = :id ";
            params.addValue("id", request.getId());
        }

        return where;
    }
}
