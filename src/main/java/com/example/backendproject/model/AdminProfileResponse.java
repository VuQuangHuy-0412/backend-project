package com.example.backendproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminProfileResponse {
    private Long id;
    private String username;
    private Date createdAt;
    private String fullName;
    private String roles;
    private String permissions;
}
