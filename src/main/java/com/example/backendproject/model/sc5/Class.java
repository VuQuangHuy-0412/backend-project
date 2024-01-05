package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Class {
    private Long id;

    private String name;

    private String code;

    private String semester;

    private Long subjectId;

    private String week;

    private String dayOfWeek;

    private String timeOfDay;

    private Double timeOfClass;

    private Long languageId;

    private Integer isAssigned;

    private Long teacherId;

    private String building;

    private String room;

    private Date createdAt;

    private Date updatedAt;
}
