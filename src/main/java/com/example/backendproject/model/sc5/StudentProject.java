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

    private Long classId;

    private Integer isAssigned;

    private Date createdAt;

    private Date updatedAt;

    private String teacher1Id;

    private String teacher2Id;

    private String teacher3Id;

    private String teacherAssignedId;
}
