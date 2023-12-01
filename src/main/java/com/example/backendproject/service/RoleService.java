package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.RoleEntity;
import com.example.backendproject.repository.RoleRepository;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {

    private static final String CACHE_NAME = "ROLE_CACHE";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationContext context;

    public List<RoleEntity> list() {
        return roleRepository.findAll();
    }

    public Set<String> getSetPermissionByRoles(String roles) {
        if (StringUtils.isBlank(roles)) {
            return new HashSet<>();
        }
        List<String> codes = Arrays.stream(roles.split(",")).map(String::trim).toList();
        List<RoleEntity> roleList = new LinkedList<>();
        for (String code : codes) {
            RoleEntity role = context.getBean(this.getClass()).getRole(code);
            if (role != null) {
                roleList.add(role);
            }
        }
        Set<String> listResult = new HashSet<>();
        for (RoleEntity roleEntity : roleList) {
            List<String> permissions = Arrays.stream(roleEntity.getPermissions().split(",")).map(String::trim).toList();
            listResult.addAll(permissions);
        }
        return listResult;
    }

    //    @Cacheable(value = CACHE_NAME, key = "#code")
    public RoleEntity getRole(String code) {
        log.info("get role by role_code = {}", code);
        return roleRepository.findByCode(code);
    }

    public void insert(RoleEntity request) {
        if (request.getId() != null || StringUtils.isBlank(request.getCode())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (StringUtils.isBlank(request.getPermissions()) || StringUtils.isBlank(request.getDescription())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        request.setPermissions(makeCorrectPermission(request.getPermissions()));
        request.setCode(request.getCode().trim());
        RoleEntity entity = roleRepository.findByCode(request.getCode());
        if (entity != null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mã vai trò đã tồn tại");
        }
        roleRepository.save(request);
    }

    public void update(RoleEntity request) {
        log.info("begin update role with request {}", CommonUtil.toJson(request));
        if (request.getId() == null || StringUtils.isBlank(request.getPermissions())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (StringUtils.isBlank(request.getPermissions()) || StringUtils.isBlank(request.getDescription())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        request.setPermissions(makeCorrectPermission(request.getPermissions()));
        Optional<RoleEntity> optionalRole = roleRepository.findById(request.getId());
        if (optionalRole.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        RoleEntity role = optionalRole.get();
        role.setDescription(request.getDescription());
        role.setPermissions(request.getPermissions());
        roleRepository.save(role);
    }

    private String makeCorrectPermission(String input) {
        Set<String> permissionSet = Arrays.stream(input.split(",")).map(String::trim).collect(Collectors.toSet());
        if (permissionSet.isEmpty() || permissionSet.stream().anyMatch(e -> PermissionServiceHelper.getPermissions().get(e) == null)) {
            log.info("permissions is not correct");
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = permissionSet.iterator();
        sb.append(iterator.next());
        while (iterator.hasNext()) {
            sb.append(", ").append(iterator.next());
        }
        return sb.toString();
    }

}
