package com.example.backendproject.repository.sc5.custom;

import com.example.backendproject.model.sc5.Dataset;
import com.example.backendproject.model.sc5.DatasetSearchRequest;

import java.util.List;

public interface DatasetRepositoryCustom {
    List<Dataset> searchDatasetByFilter(DatasetSearchRequest request);
}
