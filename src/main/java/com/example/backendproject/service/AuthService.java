package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.GrantType;
import com.example.backendproject.config.constant.RedisKey;
import com.example.backendproject.config.constant.UserStatusEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.UserEntity;
import com.example.backendproject.model.AccessTokenPayload;
import com.example.backendproject.model.auth.LoginPwdResponse;
import com.example.backendproject.model.auth.LoginRequest;
import com.example.backendproject.model.auth.LoginResponse;
import com.example.backendproject.model.auth.VerifyLoginOTPRequest;
import com.example.backendproject.repository.UserRepository;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthService {
    public static Integer ACCESS_TOKEN_EXPIRED_MINUTES = 15;
    public static Integer REFRESH_TOKEN_EXPIRED_HOURS = 50;

    @Autowired
    private CipherService cipherService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminLogService adminLogService;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public LoginPwdResponse login(LoginRequest request) {
        log.info("User login: {}", request.getUsername());

        if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        UserEntity userEntity = userRepository.findByUserName(username);

        if (userEntity == null) {
            throw new Sc5Exception(ErrorEnum.ADMIN_INVALID_CREDENTIALS, "5");
        }

        checkPassword(username, password, userEntity);
        String uuid = CommonUtil.generateRandomUUID();
        adminLogService.log(userEntity.getId(), username, "login", "login with password - " + uuid);

        LoginPwdResponse response = new LoginPwdResponse();
        response.setNeedRegistrySmartOtp(false);
        if (StringUtils.isBlank(userEntity.getSotpId())) {
            log.info("user is not active soft otp");
//            twoFactorAuthenticationService.initSms(userEntity.getMobile());
            response.setNeedRegistrySmartOtp(true);
        }

        response.setRequestId(uuid);
        String redisKey = RedisKey.OTP_PREFIX + response.getRequestId();
        this.redisTemplate.opsForValue().set(redisKey, CommonUtil.toJson(userEntity), 3, TimeUnit.MINUTES);
        return response;
    }

    public LoginResponse active2FA(VerifyLoginOTPRequest verifyRequest) {
        String redisKey = RedisKey.OTP_PREFIX + verifyRequest.getRequestId();
        String value = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(value)) {
            throw new Sc5Exception(ErrorEnum.OTP_AlREADY_CONFIRM);
        }
        UserEntity userEntity = CommonUtil.fromJson(value, UserEntity.class);

//        twoFactorAuthenticationService.active(verifyRequest.getOtp(), userEntity.getSotpId());

        Optional<UserEntity> optional = userRepository.findById(userEntity.getId());
        if (optional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.SYSTEM_ERROR);
        }
        UserEntity user = optional.get();
        user.setSotpId(userEntity.getSotpId());
        userRepository.save(user);
        redisTemplate.delete(redisKey);
        adminLogService.log(userEntity.getId(), userEntity.getUserName(), "active2FA", verifyRequest.getRequestId());
        return createLoginResponse(userEntity, "");
    }

    public LoginResponse verify(VerifyLoginOTPRequest verifyRequest) {
        String redisKey = RedisKey.OTP_PREFIX + verifyRequest.getRequestId();
        String value = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(value)) {
            throw new Sc5Exception(ErrorEnum.OTP_AlREADY_CONFIRM);
        }
        UserEntity userEntity = CommonUtil.fromJson(value, UserEntity.class);
//        twoFactorAuthenticationService.verify(verifyRequest.getOtp(), userEntity.getSotpId());

        redisTemplate.delete(redisKey);
        adminLogService.log(userEntity.getId(), userEntity.getUserName(), "verify-by-sotp", verifyRequest.getRequestId());
        return createLoginResponse(userEntity, "");
    }

    private LoginResponse createLoginResponse(UserEntity userEntity, String grantType) {
        LoginResponse ret = new LoginResponse();

        if (GrantType.REFRESH_TOKEN.equals(grantType)) {
            String accessToken = generateAccessToken(userEntity);
            ret.setAccessToken(accessToken);
            ret.setAccessTokenExpiredIn(ACCESS_TOKEN_EXPIRED_MINUTES * 60);
            return ret;
        }
        String redisKey = RedisKey.ADMIN_ACCESS_TOKENS_PREFIX + userEntity.getId();
        this.redisTemplate.delete(redisKey);
        String accessToken = generateAccessToken(userEntity);
        String refreshToken = generateRefreshToken(userEntity);
        ret.setAccessToken(accessToken);
        ret.setRefreshToken(refreshToken);
        ret.setAccessTokenExpiredIn(ACCESS_TOKEN_EXPIRED_MINUTES * 60);
        return ret;
    }

    public void invalidateSession(Long userId) {
        String redisKey = RedisKey.ADMIN_ACCESS_TOKENS_PREFIX + userId;
        this.redisTemplate.delete(redisKey);
    }

    public String generateAccessToken(UserEntity userEntity) {
        AccessTokenPayload accessTokenPayload = new AccessTokenPayload();
        accessTokenPayload.setUserId(userEntity.getId());
        accessTokenPayload.setUsername(userEntity.getUserName());
        accessTokenPayload.setRoles(userEntity.getRoles());
        accessTokenPayload.setExpiredTime(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRED_MINUTES * 60 * 1000));
        accessTokenPayload.setPadding(RandomStringUtils.random(50));

        String accessToken = cipherService.encrypt(accessTokenPayload);
        String accessTokenHash = cipherService.adminHash(accessToken);

        String redisKey = RedisKey.ADMIN_ACCESS_TOKENS_PREFIX + accessTokenPayload.getUserId();
        this.redisTemplate.opsForSet().add(redisKey, accessTokenHash);

        return accessToken;
    }

    public String generateRefreshToken(UserEntity userEntity) {
        AccessTokenPayload tokenPayload = new AccessTokenPayload();
        tokenPayload.setUserId(userEntity.getId());
        tokenPayload.setUsername(userEntity.getUserName());
        tokenPayload.setRoles(userEntity.getRoles());
        tokenPayload.setExpiredTime(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRED_HOURS * 60 * 60 * 1000));
        tokenPayload.setPadding(RandomStringUtils.random(200));

        String token = cipherService.encrypt(tokenPayload);
        String tokenHash = cipherService.adminHash(token);

        String redisKey = RedisKey.ADMIN_REFRESH_TOKEN_PREFIX + tokenPayload.getUserId();
        this.redisTemplate.opsForValue().set(redisKey, tokenHash, REFRESH_TOKEN_EXPIRED_HOURS, TimeUnit.HOURS);

        return token;
    }

    public void checkPassword(String username, String password, UserEntity userEntity) {
        log.info("Check password for {}", username);

        if (userEntity == null) {
            userEntity = userRepository.findByUserName(username);
        }

//        if (UserStatusEnum.LOCKED.getCode().equals(userEntity.getStatus())) {
//            throw new GhtkException(ErrorEnum.USER_IS_LOCKED);
//        }

        Integer failCount = userEntity.getLoginFailCount();
        if (!cipherService.check(password, userEntity.getPassword())) {
            failCount++;
            log.info("Wrong password {} times", failCount);

            userEntity.setLoginFailCount(failCount);
            userEntity.setLastFailLogin(new Date());
            userRepository.save(userEntity);

            if (failCount < 5) {
                throw new Sc5Exception(ErrorEnum.ADMIN_INVALID_CREDENTIALS, "5");
            }
            userEntity.setStatus(UserStatusEnum.LOCKED.getCode());
            userRepository.save(userEntity);
            throw new Sc5Exception(ErrorEnum.USER_IS_LOCKED);
        }

        // success
        if (failCount > 0) {
            userEntity.setLoginFailCount(0);
            userRepository.save(userEntity);
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        log.info("Refresh token");

        if (StringUtils.isBlank(refreshToken)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        AccessTokenPayload tokenPayload = cipherService.decrypt(refreshToken, AccessTokenPayload.class);

        if (tokenPayload.getExpiredTime() < System.currentTimeMillis()) {
            throw new Sc5Exception(ErrorEnum.INVALID_ACCESS_TOKEN);
        }

        String redisKey = RedisKey.ADMIN_REFRESH_TOKEN_PREFIX + tokenPayload.getUserId();
        String storedTokenHash = this.redisTemplate.opsForValue().get(redisKey);
        if (!cipherService.check(refreshToken, storedTokenHash)) {
            throw new Sc5Exception(ErrorEnum.INVALID_ACCESS_TOKEN);
        }

        UserEntity userEntity = userRepository.findById(tokenPayload.getUserId()).get();

        return createLoginResponse(userEntity, GrantType.REFRESH_TOKEN);
    }

    public void logout(Long userId) {
        log.info("User logout: {}", userId);

        String redisKey = RedisKey.ADMIN_ACCESS_TOKENS_PREFIX + userId;
        this.redisTemplate.delete(redisKey);

        String redisRefreshKey = RedisKey.ADMIN_REFRESH_TOKEN_PREFIX + userId;
        this.redisTemplate.delete(redisRefreshKey);

        adminLogService.log("logout", null);
    }

}
