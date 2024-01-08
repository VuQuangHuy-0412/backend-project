package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConstraintSearchResponse {
    private List<CustomConstraint> customConstraints;
    private List<RequiredConstraint> requiredConstraints;
    private List<ObjectiveFunction> objectiveFunctions;
}
