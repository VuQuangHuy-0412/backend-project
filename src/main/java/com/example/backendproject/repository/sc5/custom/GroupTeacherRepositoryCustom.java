package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.GroupTeacher;
import com.example.backendproject.model.sc5.GroupTeacherSearchRequest;

import java.util.List;

public interface GroupTeacherRepositoryCustom {
    List<GroupTeacher> searchGroupTeacherByFilter(GroupTeacherSearchRequest request);
}
