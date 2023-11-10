package com.example.backendproject.repository;

import com.example.backendproject.entity.UserLogEntity;
import com.example.backendproject.model.SearchAdminLogRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserLogRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public List<UserLogEntity> list(SearchAdminLogRequest request) {
        int pageNum = request.getPage() == null ? 1 : request.getPage();
        int numRow = request.getPageSize() == null ? 20 : request.getPageSize();

        List params = new ArrayList<>();
        String where = buildWhereCommand(request, params);
        params.add((pageNum - 1) * numRow);
        params.add(numRow);
        String sql = "select * from user_log " + where + " order by created_at desc limit ?, ?";

        Query query = entityManager.createNativeQuery(sql, UserLogEntity.class);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return (List<UserLogEntity>) query.getResultList();
    }

    public int count(SearchAdminLogRequest request) {
        List params = new ArrayList<>();
        String where = buildWhereCommand(request, params);
        String sql = "select count(*) from user_log " + where;

        Query query = entityManager.createNativeQuery(sql);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return ((Number) query.getSingleResult()).intValue();
    }

    private String buildWhereCommand(SearchAdminLogRequest request, List params) {
        StringBuilder sb = new StringBuilder();
        sb.append(" where 1 = 1");
        if (StringUtils.isNotEmpty(request.getUsername())) {
            sb.append(" and username = ?");
            params.add(request.getUsername());
        }
        if (request.getIpAddress() != null) {
            sb.append(" and ip_address = ?");
            params.add(request.getIpAddress());
        }
        if (request.getAction() != null) {
            sb.append(" and action = ?");
            params.add(request.getAction());
        }
        if (request.getFromDate() != null) {
            sb.append(" and created_at >= ?");
            params.add(request.getFromDate());
        }
        if (request.getToDate() != null) {
            sb.append(" and created_at < ?");
            params.add(request.getToDate());
        }
        return sb.toString();
    }


}
