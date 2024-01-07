package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentProject {
    private Long id;

    private String name;

    private String studentCode;

    private Double timeHd;

    private Integer isAssigned;

    private Date createdAt;

    private Date updatedAt;

    private Long teacher1Id;

    private Long teacher2Id;

    private Long teacher3Id;

    private Long teacherAssignedId;

    private Teacher teacher1;

    private Teacher teacher2;

    private Teacher teacher3;

    private Teacher teacherAssigned;

    private String projectType;

    private String classId;

    private String projectName;
}
