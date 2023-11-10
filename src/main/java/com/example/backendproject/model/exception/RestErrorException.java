package com.example.backendproject.model.exception;

import lombok.Data;

@Data
public class RestErrorException extends RuntimeException {
    private int httpStatus;
    private String body;
}
