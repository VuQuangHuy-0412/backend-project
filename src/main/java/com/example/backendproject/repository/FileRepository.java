package com.example.backendproject.repository;

import com.example.backendproject.entity.FileEntity;
import com.example.backendproject.repository.custom.FileRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long>, FileRepositoryCustom {

    Optional<FileEntity> findByDuplicateKey(String duplicateKey);
}
