package com.example.backendproject.repository;

import com.example.backendproject.entity.UserAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserAccessTokenRepository extends JpaRepository<UserAccessTokenEntity, Long> {
    List<UserAccessTokenEntity> findAllByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from user_access_token where user_id = :userId")
    void removeUserAccessToken(Long userId);
}
