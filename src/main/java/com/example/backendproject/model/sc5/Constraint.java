package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Constraint {
    private Long id;

    private Long teacherId;

    private Long classId;

    private String compare;

    private String columnCompare;

    private String valueCompare;

    private Integer status;
}
