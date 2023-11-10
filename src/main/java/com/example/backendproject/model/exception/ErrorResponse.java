package com.example.backendproject.model.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private Integer httpStatus;
    private String code;
    private String message;
    private String traceId;
    private Boolean success = false; // dùng cho mobile, theo format trước đây các hệ thống đang dùng
    private Map<String, String> data;
}
