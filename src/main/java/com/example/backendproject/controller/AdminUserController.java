package com.example.backendproject.controller;

import com.example.backendproject.entity.UserEntity;
import com.example.backendproject.model.AdminProfileResponse;
import com.example.backendproject.model.ChangePasswordRequest;
import com.example.backendproject.model.SearchAdminRequest;
import com.example.backendproject.model.auth.*;
import com.example.backendproject.service.AdminUserService;
import com.example.backendproject.service.AuthService;
import com.example.backendproject.util.AdminCommonUtil;
import com.example.backendproject.util.ApiDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
public class AdminUserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping(value = "/login")
    @ApiDescription(checkRole = false, show = false)
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping(value = "/auth/token/refresh")
    @ApiDescription(checkRole = false, show = false)
    public LoginResponse refreshToken(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @GetMapping(value = "/profile")
    @ApiDescription(checkRole = false, show = false)
    public AdminProfileResponse getProfile() {
        Long userId = AdminCommonUtil.getCurrentUserId();
        return adminUserService.getProfile(userId);
    }

    @PostMapping(value = "/logout")
    @ApiDescription(checkRole = false, show = false)
    public void logout() {
        Long userId = AdminCommonUtil.getCurrentUserId();
        authService.logout(userId);
    }

    @PostMapping(value = "/change-password")
    @ApiDescription(checkRole = false, show = false)
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = AdminCommonUtil.getCurrentUserId();
        adminUserService.changePassword(userId, request);
    }

    @PostMapping(value = "/admin/user/count")
    @ApiDescription(equal = "admin_user_list", show = false)
    public int count(@RequestBody SearchAdminRequest request) {
        return adminUserService.count(request);
    }

    @PostMapping(value = "/admin/user/list")
    @ApiDescription(value = "Danh sách user của hệ thống admin ewallet", code = "admin_user_list")
    public List<UserEntity> list(@RequestBody SearchAdminRequest request) {
        return adminUserService.list(request);
    }

    @PostMapping(value = "/admin/user/insert")
    @ApiDescription(value = "Thêm mới user của hệ thống admin ewallet", code = "admin_user_insert")
    public void insert(@RequestBody UserEntity request) {
        adminUserService.insert(request);
    }

    @PostMapping(value = "/admin/user/status/update")
    @ApiDescription(value = "Cập nhật trạng thái user hệ thống admin ewallet", code = "admin_user_update_status")
    public void updateStatus(@RequestBody UserEntity request) {
        adminUserService.updateStatus(request);
    }

    @PostMapping(value = "/admin/user/role/update")
    @ApiDescription(value = "Cập nhật vài trò cho user hệ thống admin ewallet", code = "admin_user_update_roles")
    public void updateRoles(@RequestBody UserEntity request) {
        adminUserService.updateRoles(request);
    }

    @PostMapping(value = "/admin/user/password/update")
    @ApiDescription(value = "Cập nhật mật khẩu cho user (Admin)", code = "admin_user_update_password")
    public void changeUserPassword(@RequestBody UserEntity request) {
        Long userId = AdminCommonUtil.getCurrentUserId();
        adminUserService.updateUserPassword(userId, request);
    }

}
