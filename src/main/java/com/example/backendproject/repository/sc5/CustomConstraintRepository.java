package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.CustomConstraintEntity;
import com.example.backendproject.repository.sc5.custom.CustomConstraintRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomConstraintRepository extends JpaRepository<CustomConstraintEntity, Long>, CustomConstraintRepositoryCustom {
}
