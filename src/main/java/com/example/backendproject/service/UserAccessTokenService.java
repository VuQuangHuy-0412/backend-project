package com.example.backendproject.service;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.UserAccessTokenEntity;
import com.example.backendproject.repository.UserAccessTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAccessTokenService {
    private final UserAccessTokenRepository userAccessTokenRepository;

    public UserAccessTokenService(UserAccessTokenRepository userAccessTokenRepository) {
        this.userAccessTokenRepository = userAccessTokenRepository;
    }

    public void saveNewAccessToken(Long userId, String accessToken) {
        UserAccessTokenEntity entity = new UserAccessTokenEntity();
        entity.setUserId(userId);
        entity.setAccessToken(accessToken);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());

        try {
            userAccessTokenRepository.save(entity);
        } catch (Exception exception) {
            log.error("Save new user access token failed", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public Set<String> getListAccessTokenByUserId(Long userId) {
        List<UserAccessTokenEntity> accessTokens = userAccessTokenRepository.findAllByUserId(userId);

        return accessTokens.stream().map(UserAccessTokenEntity::getAccessToken).collect(Collectors.toSet());
    }

    public void deleteAllAccessTokenByUserId(Long userId) {
        userAccessTokenRepository.removeUserAccessToken(userId);
    }
}
