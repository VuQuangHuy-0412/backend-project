package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.ConstraintEntity;
import com.example.backendproject.repository.sc5.custom.ConstraintRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstraintRepository extends JpaRepository<ConstraintEntity, Long>, ConstraintRepositoryCustom {
}
