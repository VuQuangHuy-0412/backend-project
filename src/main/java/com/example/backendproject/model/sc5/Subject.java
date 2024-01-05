package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Subject {
    private Long id;

    private String name;

    private String code;

    private Long groupId;

    private Date createdAt;

    private Date updatedAt;

    private GroupTeacher groupTeacher;
}
