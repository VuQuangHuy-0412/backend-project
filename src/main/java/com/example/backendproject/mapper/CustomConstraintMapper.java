package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.CustomConstraintEntity;
import com.example.backendproject.model.sc5.CustomConstraint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomConstraintMapper {
    CustomConstraint toDto(CustomConstraintEntity customConstraintEntity);

    List<CustomConstraint> toDtos(List<CustomConstraintEntity> constraintEntities);

    CustomConstraintEntity toEntity(CustomConstraint customConstraint);

    List<CustomConstraintEntity> toEntities(List<CustomConstraint> customConstraints);
}
