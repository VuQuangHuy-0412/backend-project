package com.example.backendproject.service;

import com.example.backendproject.entity.UserLogEntity;
import com.example.backendproject.model.AccessTokenPayload;
import com.example.backendproject.model.SearchAdminLogRequest;
import com.example.backendproject.repository.UserLogRepository;
import com.example.backendproject.repository.UserLogRepositoryCustom;
import com.example.backendproject.util.AdminCommonUtil;
import com.example.backendproject.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdminLogService {

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private UserLogRepositoryCustom userLogRepositoryCustom;

    public void log(String action, String data) {
        AccessTokenPayload accessTokenPayload = AdminCommonUtil.getCurrentAdminAccessTokenPayload();
        Long userId = accessTokenPayload.getUserId();
        String username = accessTokenPayload.getUsername();
        this.log(userId, username, action, data);
    }

    public void log(Long userId, String userName, String action, String data) {
        if (data != null && data.length() > 5000) {
            data = data.substring(0, 5000);
        }

        UserLogEntity userLogEntity = new UserLogEntity();
        userLogEntity.setCreatedAt(new Date());
        userLogEntity.setUserId(userId);
        userLogEntity.setUserName(userName);
        userLogEntity.setIpAddress(CommonUtil.getClientIp());
        userLogEntity.setAction(action.toLowerCase());
        userLogEntity.setData(data);

        userLogRepository.save(userLogEntity);
    }

    public List<UserLogEntity> list(SearchAdminLogRequest request) {
        return userLogRepositoryCustom.list(request);
    }

    public int count(SearchAdminLogRequest request) {
        return userLogRepositoryCustom.count(request);
    }
}
