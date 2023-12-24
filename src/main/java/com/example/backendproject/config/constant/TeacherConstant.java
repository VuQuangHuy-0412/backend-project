package com.example.backendproject.config.constant;

import java.util.List;

public class TeacherConstant {
    public static class Status {
        public static final Integer ACTIVE = 1;
        public static final Integer INACTIVE = 0;
        public static final List<Integer> LIST_STATUS_VALID = List.of(ACTIVE, INACTIVE);
    }
}
