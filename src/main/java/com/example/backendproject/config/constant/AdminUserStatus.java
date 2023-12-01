package com.example.backendproject.config.constant;

import java.util.List;

public class AdminUserStatus {
    public static final Integer ACTIVE = 1;
    public static final Integer LOCKED = 2;
    public static final List<Integer> LIST_STATUS = List.of(ACTIVE, LOCKED);
}
