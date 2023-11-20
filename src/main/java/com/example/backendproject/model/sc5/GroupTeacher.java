package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GroupTeacher {
    private Long id;

    private String name;

    private String description;

    private Long leader;

    private Date createdAt;

    private Date updatedAt;
}
