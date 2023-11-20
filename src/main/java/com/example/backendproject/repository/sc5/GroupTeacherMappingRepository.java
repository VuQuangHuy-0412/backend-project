package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupTeacherMappingRepository extends JpaRepository<GroupTeacherMappingEntity, Long> {
}
