package com.example.backendproject.repository;

import com.example.backendproject.entity.UserRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, Long> {
    UserRefreshTokenEntity findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from user_refresh_token where user_id = :userId")
    void removeUserRefreshToken(Long userId);
}
