package com.example.backendproject.model.sc5;

import com.example.backendproject.model.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TeacherSearchRequest extends BaseFilterRequest {
    private Long id;
    private String fullName;
    private String rankAndDegree;
    private String startTimeFrom;
    private String startTimeTo;
    private Long groupTeacher;
    private List<Long> ids;
}
