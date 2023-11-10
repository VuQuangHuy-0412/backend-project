package com.example.backendproject.controller;

import com.example.backendproject.entity.PermissionEntity;
import com.example.backendproject.model.PermissionModel;
import com.example.backendproject.service.AccountRoleService;
import com.example.backendproject.service.PermissionServiceHelper;
import com.example.backendproject.util.ApiDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class PermissionController {
    private final AccountRoleService accountRoleService;

    public PermissionController(AccountRoleService accountRoleService) {
        this.accountRoleService = accountRoleService;
    }

    @GetMapping(value = "/permission/list")
    @ApiDescription(value = "Danh sách quyền của hệ thống", code = "permission_list")
    public Map<String, String> list() {
        return PermissionServiceHelper.getPermissions();
    }

    @GetMapping(value = "/permissions")
    @ApiDescription(value = "Danh sách quyền", code = "permission_list")
    public List<PermissionEntity> getPermissions() {
        return accountRoleService.getPermissions();
    }

    @GetMapping(value = "/permission/groups")
    @ApiDescription(value = "Danh sách nhóm quyền", code = "permission_group_list")
    public List<PermissionModel> listPermissionFlat() {
        return accountRoleService.listPermissionFlat();
    }

    @PostMapping(value = "/permission/add")
    @ApiDescription(value = "Thêm mới nhóm quyền", code = "permission_group_add")
    public void addPermissionGroup(@RequestBody PermissionEntity request) {
        accountRoleService.addPermissionsByGroup(request);
    }

    @PostMapping(value = "/permission/update")
    @ApiDescription(value = "Sửa nhóm quyền", code = "permission_group_update")
    public void updatePermission(@RequestBody PermissionEntity request) {
        accountRoleService.updatePermission(request);
    }

    @PostMapping(value = "/permission/delete")
    @ApiDescription(value = "Xóa nhóm quyền", code = "permission_group_delete")
    public void deletePermission(@RequestBody PermissionEntity request) {
        accountRoleService.deletePermission(request);
    }
}
