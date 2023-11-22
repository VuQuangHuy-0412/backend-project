package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.model.sc5.Class;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    Class toDto(ClassEntity classEntity);

    ClassEntity toEntity(Class classDto);
}
