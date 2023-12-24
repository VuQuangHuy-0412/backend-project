package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.ConstraintSearchRequest;
import com.example.backendproject.model.sc5.CustomConstraint;

import java.util.List;

public interface CustomConstraintRepositoryCustom {
    List<CustomConstraint> searchCustomConstraintByFilter(ConstraintSearchRequest request);
}
