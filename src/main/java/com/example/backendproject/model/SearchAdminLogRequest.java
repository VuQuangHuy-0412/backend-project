package com.example.backendproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAdminLogRequest {
    private String username;
    private String action;
    private String ipAddress;
    private Date fromDate;
    private Date toDate;
    private Integer page;
    private Integer pageSize;
}
