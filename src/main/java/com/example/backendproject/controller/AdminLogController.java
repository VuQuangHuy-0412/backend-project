package com.example.backendproject.controller;

import com.example.backendproject.entity.UserLogEntity;
import com.example.backendproject.model.SearchAdminLogRequest;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.ApiDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class AdminLogController {

    @Autowired
    AdminLogService adminLogService;

    @PostMapping(value = "/admin/user/log/list")
    @ApiDescription(value = "Log hệ thống", code = "admin_log_list")
    public List<UserLogEntity> list(@RequestBody SearchAdminLogRequest request) {
        return adminLogService.list(request);
    }

    @PostMapping(value = "/admin/user/log/count")
    @ApiDescription(equal = "admin_log_list", show = false)
    public int count(@RequestBody SearchAdminLogRequest request) {
        return adminLogService.count(request);
    }

}
