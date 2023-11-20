package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
}
