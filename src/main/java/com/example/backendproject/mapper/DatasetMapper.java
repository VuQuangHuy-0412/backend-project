package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.DatasetEntity;
import com.example.backendproject.model.sc5.Dataset;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DatasetMapper {
    Dataset toDto(DatasetEntity datasetEntity);

    DatasetEntity toEntity(Dataset dataset);

    List<DatasetEntity> toEntities(List<Dataset> datasets);
}
