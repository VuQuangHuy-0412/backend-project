package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequiredConstraint {
    private Long id;

    private String code;

    private String value;

    private Integer status;

    private String type;

    private Date createdAt;

    private Date updatedAt;
}
