package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.model.sc5.Teacher;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    Teacher toDto(TeacherEntity teacherEntity);

    TeacherEntity toEntity(Teacher teacher);

    List<TeacherEntity> toEntities(List<Teacher> teachers);
}
