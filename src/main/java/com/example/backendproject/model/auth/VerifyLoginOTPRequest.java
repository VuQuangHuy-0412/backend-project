package com.example.backendproject.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyLoginOTPRequest {
    private String requestId;
    private String otp;
}
