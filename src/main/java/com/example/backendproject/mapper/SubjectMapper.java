package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.model.sc5.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    Subject toDto(SubjectEntity subjectEntity);

    SubjectEntity toEntity(Subject subject);
}
