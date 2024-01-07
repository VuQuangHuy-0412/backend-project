package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTeacherMappingRepository extends JpaRepository<GroupTeacherMappingEntity, Long> {
    List<GroupTeacherMappingEntity> findAllByTeacherIdAndDataset(Long teacherId, Long dataset);

    List<GroupTeacherMappingEntity> findAllByGroupIdAndDataset(Long groupId, Long dataset);

    List<GroupTeacherMappingEntity> findByGroupIdAndTeacherIdAndDataset(Long groupId, Long teacherId, Long dataset);
    List<GroupTeacherMappingEntity> findByDataset(Long dataset);
}
