package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.repository.sc5.custom.ClassRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long>, ClassRepositoryCustom {
    List<ClassEntity> findByTeacherId(Long teacherId);

    List<ClassEntity> findByCode(String code);
}
