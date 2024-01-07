package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GroupTeacherDetail {
    private Long id;

    private String name;

    private String description;

    private Long leader;

    private Date createdAt;

    private Date updatedAt;

    private Teacher leaderInfo;

    private List<Teacher> members;

    private Long dataset;
}
