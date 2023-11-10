package com.example.backendproject.controller;

import com.example.backendproject.entity.RoleEntity;
import com.example.backendproject.service.RoleService;
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
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping(value = "/role/list")
    @ApiDescription(value = "Danh sách role", code = "role_list")
    public List<RoleEntity> list() {
        return roleService.list();
    }

    @PostMapping(value = "/role/insert")
    @ApiDescription(value = "Thêm mới role", code = "role_insert")
    public void insert(@RequestBody RoleEntity request) {
        roleService.insert(request);
    }

    @PostMapping(value = "/role/update")
    @ApiDescription(value = "Cập nhật role", code = "role_update")
    public void update(@RequestBody RoleEntity request) {
        roleService.update(request);
    }

}
