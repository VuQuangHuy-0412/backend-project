package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.TimetablingProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetablingProcessRepository extends JpaRepository<TimetablingProcessEntity, Long> {
    TimetablingProcessEntity findByType(String type);
}
