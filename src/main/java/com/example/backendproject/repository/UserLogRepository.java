package com.example.backendproject.repository;

import com.example.backendproject.entity.UserLogEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends CrudRepository<UserLogEntity, Long> {
}
