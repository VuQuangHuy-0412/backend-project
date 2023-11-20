package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Language {
    private Long id;

    private String name;

    private Date createdAt;

    private Date updatedAt;
}
