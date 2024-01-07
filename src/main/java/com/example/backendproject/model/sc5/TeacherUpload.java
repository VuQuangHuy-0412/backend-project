package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TeacherUpload {

    private String fullName;

    private String rankAndDegree;

    private Date startTime;

    private Date birthday;

    private Float rating;

    private Integer status;

    private Integer totalTime;
}
