package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Dataset {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}
