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

    private Subject subject;

    private Teacher teacher;

    private Language language;

    private Integer startTime;

    private Integer endTime;

    private Integer numberOfStudent;

    private Integer numberOfCredits;

    private String classType;

    private String program;

    private Long dataset;
}
