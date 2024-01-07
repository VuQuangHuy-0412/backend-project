package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.repository.sc5.custom.TeacherRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Long>, TeacherRepositoryCustom {
    List<TeacherEntity> findAllByStatusAndDataset(Integer status, Long dataset);

    List<TeacherEntity> findByFullNameAndDataset(String fullName, Long dataset);

    List<TeacherEntity> findByDataset(Long dataset);
}
