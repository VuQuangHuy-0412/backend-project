package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.model.sc5.ClassSearchRequest;

import java.util.List;

public interface ClassRepositoryCustom {
    List<Class> searchClassByFilter(ClassSearchRequest request);
}
