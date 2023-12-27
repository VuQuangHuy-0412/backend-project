package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.repository.sc5.custom.StudentProjectRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentProjectRepository extends JpaRepository<StudentProjectEntity, Long>, StudentProjectRepositoryCustom {
    List<StudentProjectEntity> findByTeacherAssignedId(Long teacherId);
}
