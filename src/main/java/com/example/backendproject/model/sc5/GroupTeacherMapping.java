package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupTeacherMapping {
    private Long id;

    private Long teacherId;

    private Long groupId;

    private String role;

    private Long dataset;
}
