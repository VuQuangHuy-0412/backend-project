package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.RequiredConstraintEntity;
import com.example.backendproject.model.sc5.RequiredConstraint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequiredConstraintMapper {
    RequiredConstraint toDto(RequiredConstraintEntity RequiredConstraintEntity);

    List<RequiredConstraint> toDtos(List<RequiredConstraintEntity> constraintEntities);

    RequiredConstraintEntity toEntity(RequiredConstraint RequiredConstraint);

    List<RequiredConstraintEntity> toEntities(List<RequiredConstraint> RequiredConstraints);
}
