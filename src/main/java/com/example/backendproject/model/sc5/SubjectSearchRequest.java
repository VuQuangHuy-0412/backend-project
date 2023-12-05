package com.example.backendproject.model.sc5;

import com.example.backendproject.model.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectSearchRequest extends BaseFilterRequest {
    private Long id;
    private String name;
    private String code;
}
