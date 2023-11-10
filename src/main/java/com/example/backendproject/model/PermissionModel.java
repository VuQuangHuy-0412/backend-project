package com.example.backendproject.model;

import com.example.backendproject.entity.PermissionEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PermissionModel {
    private Long id;
    private String label;
    private String code;
    private Long parentId;
    private List<PermissionModel> children;

    public PermissionModel(PermissionEntity permissionEntity) {
        this.id = permissionEntity.getId();
        this.label = permissionEntity.getLabel();
        this.code = permissionEntity.getCode();
        this.parentId = permissionEntity.getParentId();
    }
}
