package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.model.sc5.GroupTeacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupTeacherMapper {
    GroupTeacher toDto(GroupTeacherEntity groupTeacherEntity);
}
