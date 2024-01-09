package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ClassUpload {
    private String name;

    private String code;

    private String semester;

    private String subjectCode;

    private String week;

    private String dayOfWeek;

    private String languageName;

    private String room;

    private String timeInDay;

    private Integer numberOfStudent;

    private String numberOfCredits;

    private String classType;

    private String program;
}
