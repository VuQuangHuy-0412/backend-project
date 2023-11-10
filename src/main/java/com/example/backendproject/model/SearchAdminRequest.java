package com.example.backendproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAdminRequest {
    private String username;
    private String fullName;
    private Integer status;
    private String role;
    private String mobile;
    private String email;
    private Date createdDateFrom;
    private Date createdDateTo;
    private Integer page;
    private Integer pageSize;
}
