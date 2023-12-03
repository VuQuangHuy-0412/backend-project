package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.UserAccessTokenEntity;
import com.example.backendproject.entity.UserRefreshTokenEntity;
import com.example.backendproject.repository.UserRefreshTokenRepository;
import com.example.backendproject.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        entity.setExpireTime(DateUtil.toDate(LocalDateTime.now().minusHours(-50)));
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
        List<UserRefreshTokenEntity> refreshTokens = userRefreshTokenRepository.findByUserId(userId);

        for (UserRefreshTokenEntity entity : refreshTokens) {
            if (entity.getExpireTime().after(new Date())) {
                return entity.getRefreshToken();
            }
        }

        return null;
    }

    public void deleteAllRefreshTokenByUserId(Long userId) {
        userRefreshTokenRepository.removeUserRefreshToken(userId);
    }
}
