package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.Teacher;
import com.example.backendproject.model.sc5.TeacherSearchRequest;

import java.util.List;

public interface TeacherRepositoryCustom {
    List<Teacher> searchTeacherByFilter(TeacherSearchRequest request);

    List<Teacher> getAllTeacherOfAGroupAndDataset(Long groupId, Long dataset);
}
