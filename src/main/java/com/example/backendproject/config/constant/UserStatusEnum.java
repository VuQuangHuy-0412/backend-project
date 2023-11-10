package com.example.backendproject.config.constant;

public enum UserStatusEnum {
    ACTIVE (1),
    LOCKED (2);

    private final Integer code;

    UserStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public boolean is(Integer code) {
        return this.code.equals(code);
    }
}
