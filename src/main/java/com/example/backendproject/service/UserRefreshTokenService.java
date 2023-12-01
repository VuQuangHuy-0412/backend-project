package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.UserRefreshTokenEntity;
import com.example.backendproject.repository.UserRefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class UserRefreshTokenService {
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public UserRefreshTokenService(UserRefreshTokenRepository userRefreshTokenRepository) {
        this.userRefreshTokenRepository = userRefreshTokenRepository;
    }

    public void saveNewRefreshToken(Long userId, String refreshToken) {
        UserRefreshTokenEntity entity = new UserRefreshTokenEntity();
        entity.setUserId(userId);
        entity.setRefreshToken(refreshToken);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());

        try {
            userRefreshTokenRepository.save(entity);
        } catch (Exception exception) {
            log.error("Save new user refresh token failed", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public String getRefreshTokenByUserId(Long userId) {
        UserRefreshTokenEntity refreshTokens = userRefreshTokenRepository.findByUserId(userId);

        return refreshTokens.getRefreshToken();
    }

    public void deleteAllRefreshTokenByUserId(Long userId) {
        userRefreshTokenRepository.removeUserRefreshToken(userId);
    }
}
