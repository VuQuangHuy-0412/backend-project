package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.repository.sc5.custom.GroupTeacherRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTeacherRepository extends JpaRepository<GroupTeacherEntity, Long>, GroupTeacherRepositoryCustom {
    List<GroupTeacherEntity> findAllByIdInAndDataset(List<Long> ids, Long dataset);

    List<GroupTeacherEntity> findByNameAndDataset(String name, Long dataset);
    List<GroupTeacherEntity> findByDataset(Long dataset);
}
