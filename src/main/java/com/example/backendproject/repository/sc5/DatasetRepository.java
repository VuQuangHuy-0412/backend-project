package com.example.backendproject.repository.sc5;

import com.example.backendproject.entity.sc5.DatasetEntity;
import com.example.backendproject.repository.sc5.custom.DatasetRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<DatasetEntity, Long>, DatasetRepositoryCustom {
}
