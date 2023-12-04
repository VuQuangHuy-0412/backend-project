package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.GrantType;
import com.example.backendproject.config.constant.UserStatusEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.UserEntity;
import com.example.backendproject.model.AccessTokenPayload;
import com.example.backendproject.model.auth.LoginRequest;
import com.example.backendproject.model.auth.LoginResponse;
import com.example.backendproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class AuthService {
    public static Integer ACCESS_TOKEN_EXPIRED_MINUTES = 120;
    public static Integer REFRESH_TOKEN_EXPIRED_HOURS = 50;

    @Autowired
    private CipherService cipherService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminLogService adminLogService;

    @Autowired
    private UserAccessTokenService userAccessTokenService;

    @Autowired
    private UserRefreshTokenService userRefreshTokenService;

    public LoginResponse login(LoginRequest request) {
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
        adminLogService.log(userEntity.getId(), username, "login", "login with password");
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
        userAccessTokenService.deleteAllAccessTokenByUserId(userEntity.getId());

        String accessToken = generateAccessToken(userEntity);
        String refreshToken = generateRefreshToken(userEntity);
        ret.setAccessToken(accessToken);
        ret.setRefreshToken(refreshToken);
        ret.setAccessTokenExpiredIn(ACCESS_TOKEN_EXPIRED_MINUTES * 60);
        return ret;
    }

    public void invalidateSession(Long userId) {
        userAccessTokenService.deleteAllAccessTokenByUserId(userId);
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

        userAccessTokenService.saveNewAccessToken(accessTokenPayload.getUserId(), accessTokenHash);

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

        userRefreshTokenService.saveNewRefreshToken(tokenPayload.getUserId(), tokenHash);
        return token;
    }

    public void checkPassword(String username, String password, UserEntity userEntity) {
        log.info("Check password for {}", username);

        if (userEntity == null) {
            userEntity = userRepository.findByUserName(username);
        }

        if (UserStatusEnum.LOCKED.getCode().equals(userEntity.getStatus())) {
            throw new Sc5Exception(ErrorEnum.USER_IS_LOCKED);
        }

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

        String storedTokenHash = userRefreshTokenService.getRefreshTokenByUserId(tokenPayload.getUserId());
        if (!cipherService.check(refreshToken, storedTokenHash)) {
            throw new Sc5Exception(ErrorEnum.INVALID_ACCESS_TOKEN);
        }

        UserEntity userEntity = userRepository.findById(tokenPayload.getUserId()).get();

        return createLoginResponse(userEntity, GrantType.REFRESH_TOKEN);
    }

    public void logout(Long userId) {
        log.info("User logout: {}", userId);

        userAccessTokenService.deleteAllAccessTokenByUserId(userId);
        userRefreshTokenService.deleteAllRefreshTokenByUserId(userId);

        adminLogService.log("logout", null);
    }

}
