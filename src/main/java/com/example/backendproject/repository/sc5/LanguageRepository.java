package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {
    List<LanguageEntity> findByName(String name);
}
