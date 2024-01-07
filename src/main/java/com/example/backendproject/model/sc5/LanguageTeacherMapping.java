package com.example.backendproject.model.sc5;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageTeacherMapping {
    private Long id;

    private Long teacherId;

    private Long languageId;

    private Long dataset;
}
