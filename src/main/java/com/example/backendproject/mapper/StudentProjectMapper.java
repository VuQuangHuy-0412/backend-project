package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.model.sc5.StudentProject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentProjectMapper {
    StudentProject toDto(StudentProjectEntity studentProjectEntity);

    List<StudentProject> toDtos(List<StudentProjectEntity> studentProjectEntity);

    StudentProjectEntity toEntity(StudentProject studentProject);

    List<StudentProjectEntity> toEntities(List<StudentProject> studentProjects);
}
