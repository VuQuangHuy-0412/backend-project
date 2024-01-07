package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Teacher {
    private Long id;

    private String fullName;

    private String rankAndDegree;

    private Date startTime;

    private Date birthday;

    private Date createdAt;

    private Date updatedAt;

    private Double gdTime;

    private Double hdTime;

    private Float rating;

    private Integer status;

    private List<GroupTeacher> groupTeacher;

    private String role;

    private Integer totalTime;

    private Long dataset;
}
