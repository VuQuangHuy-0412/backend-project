package com.example.backendproject.model.sc5;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvaluateResponse {
    private List<EvaluateDetail> data;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class EvaluateDetail {
        private String key;

        private String value;
    }
}
