package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.ConstraintEntity;
import com.example.backendproject.model.sc5.Constraint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConstraintMapper {
    Constraint toDto(ConstraintEntity constraintEntity);

    List<Constraint> toDtos(List<ConstraintEntity> constraintEntities);

    ConstraintEntity toEntity(Constraint constraint);

    List<ConstraintEntity> toEntities(List<Constraint> constraints);
}
