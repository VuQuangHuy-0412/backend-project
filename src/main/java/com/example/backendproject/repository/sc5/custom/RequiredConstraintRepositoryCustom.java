package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.ConstraintSearchRequest;
import com.example.backendproject.model.sc5.RequiredConstraint;

import java.util.List;

public interface RequiredConstraintRepositoryCustom {
    List<RequiredConstraint> searchRequiredConstraintByFilter(ConstraintSearchRequest request);
}
