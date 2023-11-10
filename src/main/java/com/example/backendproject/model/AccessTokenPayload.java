package com.example.backendproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenPayload {
    private Long userId;
    private String username;
    private String roles;
    private Long expiredTime;
    private String padding;
}
