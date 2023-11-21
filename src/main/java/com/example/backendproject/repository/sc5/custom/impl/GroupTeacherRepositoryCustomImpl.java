package com.example.backendproject.repository.sc5.custom.impl;

import com.example.backendproject.model.sc5.GroupTeacher;
import com.example.backendproject.model.sc5.GroupTeacherSearchRequest;
import com.example.backendproject.repository.sc5.custom.GroupTeacherRepositoryCustom;
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
public class GroupTeacherRepositoryCustomImpl implements GroupTeacherRepositoryCustom {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public GroupTeacherRepositoryCustomImpl(@Qualifier("sc5JdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<GroupTeacher> searchGroupTeacherByFilter(GroupTeacherSearchRequest request) {
        String sql = "select gt.* from group_teacher gt where 1=1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String where = buildWhereSqlCommand(params, request);

        sql += where + " order by gt.id desc limit :offset, :limit ";
        params.addValue("offset", request.getPage() * request.getPageSize());
        params.addValue("limit", request.getPageSize());

        List<GroupTeacher> result;
        try {
            result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(GroupTeacher.class));
        } catch (Exception exception) {
            log.error("Query search group teacher failed!", exception);
            result = Collections.emptyList();
        }
        return result;
    }

    private String buildWhereSqlCommand(MapSqlParameterSource params, GroupTeacherSearchRequest request) {
        String where = "";
        if (request.getId() != null) {
            where += " and gt.id = :id ";
            params.addValue("id", request.getId());
        }

        if (!StringUtils.isBlank(request.getName())) {
            where += " and gt.name like :name ";
            params.addValue("name", "%" + request.getName() + "%");
        }

        return where;
    }
}
