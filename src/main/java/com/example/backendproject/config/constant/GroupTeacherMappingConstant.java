package com.example.backendproject.config.constant;

import java.util.List;

public class GroupTeacherMappingConstant {
    public static class Role {
        public static final String TEACHER = "teacher";
        public static final String LEADER = "leader";
        public static final List<String> LIST_ROLE = List.of(TEACHER, LEADER);
    }
}
