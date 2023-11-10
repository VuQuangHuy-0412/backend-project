package com.example.backendproject.model.auth;

import lombok.Data;

@Data
public class LoginPwdResponse {
    private String requestId;
    private boolean needRegistrySmartOtp;
}
