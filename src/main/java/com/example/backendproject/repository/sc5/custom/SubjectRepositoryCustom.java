package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.Subject;
import com.example.backendproject.model.sc5.SubjectSearchRequest;

import java.util.List;

public interface SubjectRepositoryCustom {
    List<Subject> searchSubjectByFilter(SubjectSearchRequest request);
}
