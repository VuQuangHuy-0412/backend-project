package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.RequiredConstraintEntity;
import com.example.backendproject.repository.sc5.custom.RequiredConstraintRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequiredConstraintRepository extends JpaRepository<RequiredConstraintEntity, Long>, RequiredConstraintRepositoryCustom {
}
