package com.example.backendproject.model.sc5;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimetableResponse {
    private List<TimetableDetail> data;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TimetableDetail {
        public String dayOfWeek;
        public String timeTeaching;
        public String className;
        public String classCode;
        public String week;
        public String program;
        public String languageName;
    }
}
