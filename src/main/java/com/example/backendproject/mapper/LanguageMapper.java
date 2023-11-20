package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.LanguageEntity;
import com.example.backendproject.model.sc5.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    Language toDto(LanguageEntity languageEntity);
}
