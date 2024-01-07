package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CustomConstraint {
    private Long id;

    private Long teacherId;

    private String teacherCompare;

    private String teacherColumnCompare;

    private String teacherValueCompare;

    private Long classId;

    private String classCompare;

    private String classColumnCompare;

    private String classValueCompare;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;

    private Long dataset;
}
