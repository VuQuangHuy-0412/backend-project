package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.LanguageTeacherMappingEntity;
import com.example.backendproject.model.sc5.LanguageTeacherMapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageTeacherMappingMapper {
    LanguageTeacherMapping toDto(LanguageTeacherMappingEntity languageTeacherMappingEntity);
}
