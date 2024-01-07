package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.Dataset;
import com.example.backendproject.model.sc5.DatasetSearchRequest;
import com.example.backendproject.model.sc5.DatasetSearchResponse;
import com.example.backendproject.service.sc5.DatasetService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatasetController {
    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping(value = "/dataset/search")
    @ApiDescription(value = "Danh sách bộ dữ liệu", code = "dataset_search")
    public DatasetSearchResponse searchDataset(DatasetSearchRequest request) {
        return datasetService.searchDataset(request);
    }

    @PostMapping(value = "/dataset/create")
    @ApiDescription(value = "Thêm mới bộ dữ liệu", code = "dataset_create")
    public void createDataset(@RequestBody Dataset dataset) {
        datasetService.createDataset(dataset);
    }

    @PostMapping(value = "/dataset/update")
    @ApiDescription(value = "Cập nhật thông tin bộ dữ liệu", code = "dataset_update")
    public void updateDataset(@RequestBody Dataset dataset) {
        datasetService.updateDataset(dataset);
    }
}
