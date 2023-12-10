package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTeacherToGroupRequest {
    private Long groupId;
    private Long teacherId;
    private String role;
}
