package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.repository.sc5.custom.GroupTeacherRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupTeacherRepository extends JpaRepository<GroupTeacherEntity, Long>, GroupTeacherRepositoryCustom {
}
