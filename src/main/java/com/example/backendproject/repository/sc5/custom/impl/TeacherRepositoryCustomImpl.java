package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.Teacher;
import com.example.backendproject.model.sc5.TeacherSearchRequest;
import com.example.backendproject.repository.sc5.custom.TeacherRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class TeacherRepositoryCustomImpl implements TeacherRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TeacherRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Teacher> searchTeacherByFilter(TeacherSearchRequest request) {
        String sql = "select t.* from teacher t where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = buildWhereSqlCommand(params, request);

        sql += where + " order by t.id desc limit :offset, :limit ";
        params.addValue("offset", request.getPage() * request.getPageSize());
        params.addValue("limit", request.getPageSize());

        List<Teacher> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Teacher.class));
        } catch (Exception exception) {
            log.error("Query search teacher failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }

    private String buildWhereSqlCommand(MapSqlParameterSource params, TeacherSearchRequest request) {
        String where = "";
        if (request.getId() != null) {
            where += " and t.id = :id ";
            params.addValue("id", request.getId());
        }

        if (!StringUtils.isBlank(request.getFullName())) {
            where += " and t.full_name like :fullName ";
            params.addValue("fullName", "%" + request.getFullName() + "%");
        }

        if (!StringUtils.isBlank(request.getRankAndDegree())) {
            where += " and t.rank_and_degree = :rankAndDegree ";
            params.addValue("rankAndDegree", request.getFullName());
        }

        if (!CollectionUtils.isEmpty(request.getIds())) {
            where += " and t.id in (:ids) ";
            params.addValue("ids", request.getIds());
        }

        if (StringUtils.isNotBlank(request.getStartTimeFrom())) {
            where += " and t.start_time >= :startTimeFrom ";
            params.addValue("startTimeFrom", request.getStartTimeFrom());
        }

        if (StringUtils.isNotBlank(request.getStartTimeTo())) {
            where += " and t.start_time < :startTimeTo ";
            params.addValue("startTimeTo", request.getStartTimeTo());
        }

        if (request.getStatus() != null) {
            where += " and t.status = :status ";
            params.addValue("status", request.getStatus());
        }

        return where;
    }

    @Override
    public List<Teacher> getAllTeacherOfAGroup(Long groupId) {
        String sql = "select t.*, gtm.role from group_teacher_mapping gtm " +
                " join teacher t on gtm.teacher_id = t.id " +
                " where 1=1 " +
                " and gtm.group_id = :groupId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("groupId", groupId);

        List<Teacher> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Teacher.class));
        } catch (Exception exception) {
            log.error("Query search teacher failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }
}
