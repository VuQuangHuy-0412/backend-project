package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.repository.sc5.custom.TeacherRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Long>, TeacherRepositoryCustom {
    List<TeacherEntity> findAllByStatus(Integer status);

    List<TeacherEntity> findByFullName(String fullName);
}
