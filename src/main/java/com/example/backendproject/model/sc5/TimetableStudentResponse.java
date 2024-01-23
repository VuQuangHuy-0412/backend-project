package com.example.backendproject.model.sc5;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimetableStudentResponse {
    private List<TimetableDetail> data;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TimetableDetail {
        public String studentName;
        public String studentCode;
        public String classCode;
        public String projectName;
        public String projectType;
    }
}
