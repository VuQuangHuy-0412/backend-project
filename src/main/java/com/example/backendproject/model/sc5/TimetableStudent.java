package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimetableStudent {
    private List<StudentProject> data;
    private Teacher teacher;
    private Long dataset;
}
