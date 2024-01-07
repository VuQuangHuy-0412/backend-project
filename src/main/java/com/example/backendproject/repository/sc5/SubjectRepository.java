package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.repository.sc5.custom.SubjectRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long>, SubjectRepositoryCustom {
    List<SubjectEntity> findByCode(String code);
}
