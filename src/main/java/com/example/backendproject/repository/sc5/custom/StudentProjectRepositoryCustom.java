package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.StudentProject;
import com.example.backendproject.model.sc5.StudentProjectSearchRequest;

import java.util.List;

public interface StudentProjectRepositoryCustom {
    List<StudentProject> searchStudentProjectByFilter(StudentProjectSearchRequest request);
}
