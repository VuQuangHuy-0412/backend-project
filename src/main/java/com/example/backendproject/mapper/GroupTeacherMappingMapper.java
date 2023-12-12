package com.example.backendproject.mapper;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import com.example.backendproject.model.sc5.GroupTeacher;
import com.example.backendproject.model.sc5.GroupTeacherMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupTeacherMappingMapper {
    GroupTeacherMapping toDto(GroupTeacherMappingEntity groupTeacherMappingEntity);

    List<GroupTeacherMappingEntity> toEntities(List<GroupTeacherMapping> groupTeacherMappings);
}
