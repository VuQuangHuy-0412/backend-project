package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.PermissionEntity;
import com.example.backendproject.model.PermissionModel;
import com.example.backendproject.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountRoleService {
    private final PermissionRepository permissionRepository;

    public AccountRoleService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionEntity> getPermissions() {
        return permissionRepository.findAll();
    }

    public List<PermissionModel> listPermissionFlat() {
        List<PermissionEntity> permissionGroupEntities = permissionRepository.findAll();
        return permissionGroupEntities.stream().map(PermissionModel::new).collect(Collectors.toList());
    }

    public void addPermissionsByGroup(PermissionEntity request) {
        if (request.getId() != null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (StringUtils.isBlank(request.getLabel())) {
            throw new Sc5Exception(ErrorEnum.INPUT_MISSING);
        }
        permissionRepository.save(request);
    }

    public void updatePermission(PermissionEntity request) {
        if (request.getId() == null || request.getLabel() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        Optional<PermissionEntity> optionalPermission = permissionRepository.findById(request.getId());

        if (optionalPermission.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        PermissionEntity permissionEntity = optionalPermission.get();
        permissionEntity.setCode(request.getCode());
        permissionEntity.setLabel(request.getLabel());
        permissionEntity.setParentId(request.getParentId());

        permissionRepository.save(permissionEntity);
    }

    public void deletePermission(PermissionEntity request) {
        if (request.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        Optional<PermissionEntity> optionalPermission = permissionRepository.findById(request.getId());

        if (optionalPermission.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        PermissionEntity permissionEntity = optionalPermission.get();

        permissionRepository.delete(permissionEntity);
    }
}
