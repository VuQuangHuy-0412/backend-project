package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.model.sc5.StudentProject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentProjectMapper {
    StudentProject toDto(StudentProjectEntity studentProjectEntity);
}
