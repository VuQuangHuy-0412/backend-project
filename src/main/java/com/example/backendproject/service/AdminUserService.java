package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.UserEntity;
import com.example.backendproject.model.AdminProfileResponse;
import com.example.backendproject.model.ChangePasswordRequest;
import com.example.backendproject.model.SearchAdminRequest;
import com.example.backendproject.repository.UserRepository;
import com.example.backendproject.repository.UserRepositoryCustom;
import com.example.backendproject.util.AdminCommonUtil;
import com.example.backendproject.util.CommonUtil;
import com.example.backendproject.util.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CipherService cipherService;

    @Autowired
    private AdminLogService adminLogService;

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RoleServiceHelper roleServiceHelper;

    @Autowired
    private RoleService roleService;

    @Value("${password_regex}")
    private String passwordRegex;

    private static final String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    public AdminProfileResponse getProfile(Long userId) {

        UserEntity userEntity = userRepository.findById(userId).get();

        AdminProfileResponse profileResponse = new AdminProfileResponse();
        profileResponse.setId(userEntity.getId());
        profileResponse.setUsername(userEntity.getUserName());
        profileResponse.setFullName(userEntity.getFullName());
        profileResponse.setCreatedAt(userEntity.getCreatedAt());
        profileResponse.setRoles(userEntity.getRoles());
        Set<String> permissions = null;
        if (userEntity.getRoles().contains("admin")) {
            permissions = roleServiceHelper.getPermissionForAdmin();
        } else {
            permissions = roleService.getSetPermissionByRoles(userEntity.getRoles());
        }
        StringBuilder sb = new StringBuilder();
        if (!permissions.isEmpty()) {
            Iterator<String> iterator = permissions.iterator();
            sb.append(iterator.next());
            while (iterator.hasNext()) {
                sb.append(", ").append(iterator.next());
            }
        }
        profileResponse.setPermissions(sb.toString());
        return profileResponse;
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();

        ValidatorUtil.validatePasswordFormat(newPassword);

        if (oldPassword.equals(newPassword)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mật khẩu mới không được giống mật khẩu cũ");
        }

        if (!AdminCommonUtil.isValidRegex(newPassword, passwordRegex)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mật khẩu phải có độ dài tối thiếu 8 ký tự và chứa chữ cái IN HOA, in thường, chữ số, ký tự đặc biệt");
        }

        UserEntity userEntity = userRepository.findById(userId).get();

        authService.checkPassword(userEntity.getUserName(), oldPassword, userEntity);

        String newPasswordHash = cipherService.adminHash(newPassword);
        userEntity.setPassword(newPasswordHash);
        userRepository.save(userEntity);
        authService.invalidateSession(userId);
        adminLogService.log(userId, userEntity.getUserName(), "change-password", null);
    }

    public int count(SearchAdminRequest request) {
        return userRepositoryCustom.count(request);
    }

    public List<UserEntity> list(SearchAdminRequest request) {
        List<UserEntity> list = userRepositoryCustom.list(request);
        for (UserEntity e : list) {
            e.setPassword("");
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public void insert(UserEntity request) {
        if (request.getId() != null || StringUtils.isBlank(request.getUserName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (StringUtils.isBlank(request.getPassword()) || StringUtils.isBlank(request.getFullName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (!AdminCommonUtil.isValidRegex(request.getPassword(), passwordRegex)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mật khẩu phải có độ dài tối thiếu 8 ký tự và chứa chữ cái IN HOA, in thường, chữ số, ký tự đặc biệt");
        }
        if (StringUtils.isBlank(request.getRoles())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (StringUtils.isBlank(request.getMobile())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (StringUtils.isNotBlank(request.getEmail())) {
            if (!AdminCommonUtil.isValidRegex(request.getEmail(), emailRegex)) {
                throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
            }
        }
        ValidatorUtil.validateMobileFormat(request.getMobile());
        String mobile = ValidatorUtil.correctMobileFormat(request.getMobile());
        String username = request.getUserName().trim();
        UserEntity entity = userRepository.findByUserName(username);
        if (entity != null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Username đã tồn tại");
        }

        entity = new UserEntity();
        entity.setUserName(username);
        String passwordHash = cipherService.adminHash(request.getPassword().trim());
        entity.setPassword(passwordHash);
        entity.setFullName(request.getFullName().trim());
        entity.setRoles(request.getRoles().replaceAll("\\s", ""));
        entity.setLoginFailCount(0);
//        entity.setStatus(AdminUserStatus.ACTIVE);
        entity.setCreatedAt(new Date());
        entity.setMobile(mobile);
        entity.setEmail(request.getEmail());
        userRepository.save(entity);
        request.setPassword("");
        adminLogService.log("insertAdminUser", CommonUtil.toJson(request));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(UserEntity request) {
        if (request.getId() == null || request.getStatus() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
//        if (!AdminUserStatus.LIST_STATUS.contains(request.getStatus())) {
//            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
//        }
        Optional<UserEntity> entityOpt = userRepository.findById(request.getId());
        if (entityOpt.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        UserEntity entity = entityOpt.get();
        Integer oldStatus = entity.getStatus();
        if (oldStatus.equals(request.getStatus())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
//        if (AdminUserStatus.LOCKED.equals(request.getStatus())) {
//            Set<String> accessKeys = redisTemplate.keys(RedisKey.ADMIN_ACCESS_TOKENS_PREFIX + entity.getId() + "*");
//            redisTemplate.delete(accessKeys);
//            Set<String> refreshKeys = redisTemplate.keys(RedisKey.ADMIN_REFRESH_TOKEN_PREFIX + entity.getId() + "*");
//            redisTemplate.delete(refreshKeys);
//        }
        entity.setStatus(request.getStatus());
        entity.setLoginFailCount(0);
        userRepository.save(entity);
        adminLogService.log("updateAdminUserStatus", "from " + oldStatus + " to " + request.getStatus());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRoles(UserEntity request) {
        if (request.getId() == null || StringUtils.isBlank(request.getRoles())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        Optional<UserEntity> entityOpt = userRepository.findById(request.getId());
        if (entityOpt.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        UserEntity entity = entityOpt.get();
        String roles = request.getRoles();
        List<String> rolesInDB = roleService.list().stream().map(e -> e.getCode().trim()).collect(Collectors.toList());
        if (Arrays.stream(roles.split(",")).anyMatch(item -> !rolesInDB.contains(item.trim()))) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        entity.setRoles(roles);
        userRepository.save(entity);
        authService.invalidateSession(request.getId());
        adminLogService.log("updateAdminUserRoles", CommonUtil.toJson(request));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(Long userId, UserEntity request) {
        if (request.getId() == null || StringUtils.isBlank(request.getPassword())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        if (userId.equals(request.getId())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không thể thay đổi mật khẩu cho chính mình bằng chức năng này");
        }
        Optional<UserEntity> entityOpt = userRepository.findById(request.getId());
        if (entityOpt.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        UserEntity entity = entityOpt.get();
        if (!AdminCommonUtil.isValidRegex(request.getPassword(), passwordRegex)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mật khẩu phải có độ dài tối thiếu 8 ký tự và chứa chữ cái IN HOA, in thường, chữ số, ký tự đặc biệt");
        }
        String newPasswordHash = cipherService.adminHash(request.getPassword());
        entity.setPassword(newPasswordHash);
        userRepository.save(entity);
        adminLogService.log("updateAdminUserPassword", null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetSotp(UserEntity request) {
        if (request.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        Optional<UserEntity> optional = userRepository.findById(request.getId());
        if (optional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        UserEntity entity = optional.get();
        if (StringUtils.isBlank(entity.getSotpId())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        entity.setSotpId(null);
        userRepository.save(entity);
        adminLogService.log("resetSotp", "userId: " + entity.getId().toString() + ", username: " + entity.getUserName());
    }
}
