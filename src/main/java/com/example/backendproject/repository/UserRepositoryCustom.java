package com.example.backendproject.repository;

import com.example.backendproject.entity.UserEntity;
import com.example.backendproject.model.SearchAdminRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public List<UserEntity> list(SearchAdminRequest searchAdminRequest) {
        int pageNum = searchAdminRequest.getPage() == null ? 1 : searchAdminRequest.getPage();
        int numRow = searchAdminRequest.getPageSize() == null ? 20 : searchAdminRequest.getPageSize();

        List<Object> params = new ArrayList<>();
        String where = buildWhereCommand(searchAdminRequest, params);
        params.add((pageNum - 1) * numRow);
        params.add(numRow);
        String sql = "select * from user " + where + " order by created_at desc limit ?, ?";

        Query query = entityManager.createNativeQuery(sql, UserEntity.class);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return (List<UserEntity>) query.getResultList();
    }

    public int count(SearchAdminRequest searchAdminRequest) {
        List<Object> params = new ArrayList<>();
        String where = buildWhereCommand(searchAdminRequest, params);
        String sql = "select count(*) from user " + where;

        Query query = entityManager.createNativeQuery(sql);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return ((Number) query.getSingleResult()).intValue();
    }

    private String buildWhereCommand(SearchAdminRequest request, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(" where 1 = 1");
        if (StringUtils.isNotEmpty(request.getUsername())) {
            sb.append(" and username like ?");
            params.add("%" + request.getUsername() + "%");
        }
        if (request.getStatus() != null) {
            sb.append(" and status = ?");
            params.add(request.getStatus());
        }
        if (StringUtils.isNotEmpty(request.getFullName())) {
            sb.append(" and full_name like ?");
            params.add("%" + request.getFullName() + "%");
        }
        if (StringUtils.isNotEmpty(request.getMobile())) {
            sb.append(" and mobile like ?");
            params.add("%" + request.getMobile());
        }
        if (StringUtils.isNotEmpty(request.getEmail())) {
            sb.append(" and email like ?");
            params.add(request.getEmail() + "%");
        }
        if (StringUtils.isNotEmpty(request.getRole())) {
            sb.append(" and CONCAT(',',roles,',') like ? ");
            params.add("%," + request.getRole() + ",%");
        }
        if (request.getCreatedDateFrom() != null) {
            sb.append(" and created_at >= ?");
            params.add(request.getCreatedDateFrom());
        }
        if (request.getCreatedDateTo() != null) {
            sb.append(" and created_at < ?");
            params.add(request.getCreatedDateTo());
        }
        return sb.toString();
    }


}
