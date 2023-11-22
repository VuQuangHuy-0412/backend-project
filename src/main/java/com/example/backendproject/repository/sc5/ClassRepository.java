package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.repository.sc5.custom.ClassRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long>, ClassRepositoryCustom {
}
