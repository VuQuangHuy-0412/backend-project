package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.DatasetEntity;
import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.mapper.DatasetMapper;
import com.example.backendproject.model.sc5.Dataset;
import com.example.backendproject.model.sc5.DatasetSearchRequest;
import com.example.backendproject.model.sc5.DatasetSearchResponse;
import com.example.backendproject.repository.sc5.DatasetRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DatasetService {
    private final DatasetRepository datasetRepository;
    private final AdminLogService adminLogService;
    private final DatasetMapper datasetMapper;

    public DatasetService(DatasetRepository datasetRepository,
                          AdminLogService adminLogService,
                          DatasetMapper datasetMapper) {
        this.datasetRepository = datasetRepository;
        this.adminLogService = adminLogService;
        this.datasetMapper = datasetMapper;
    }

    public DatasetSearchResponse searchDataset(DatasetSearchRequest request) {
        DatasetSearchResponse response = new DatasetSearchResponse();

        List<Dataset> data = datasetRepository.searchDatasetByFilter(request);
        response.setData(data);
        return response;
    }

    public void createDataset(Dataset dataset) {
        adminLogService.log("createDataset", CommonUtil.toJson(dataset));
        log.info("Create dataset with data: " + CommonUtil.toJson(dataset));

        validateCreateDatasetRequest(dataset);
        DatasetEntity datasetEntity = datasetMapper.toEntity(dataset);
        datasetEntity.setCreatedAt(new Date());
        datasetEntity.setUpdatedAt(new Date());
        try {
            datasetRepository.save(datasetEntity);
        } catch (Exception exception) {
            log.error("Save dataset error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateDatasetRequest(Dataset dataset) {
        if (StringUtils.isBlank(dataset.getName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên bộ dữ liệu.");
        }
    }

    public void updateDataset(Dataset dataset) {
        if (dataset.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy bộ dữ liệu.");
        }
        validateCreateDatasetRequest(dataset);

        Optional<DatasetEntity> datasetEntityOptional = datasetRepository.findById(dataset.getId());
        if (datasetEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy bộ dữ liệu.");
        }

        DatasetEntity datasetEntity = datasetEntityOptional.get();
        datasetEntity.setName(dataset.getName());
        datasetEntity.setDescription(dataset.getDescription());
        datasetEntity.setUpdatedAt(new Date());

        try {
            datasetRepository.save(datasetEntity);
        } catch (Exception exception) {
            log.error("Update dataset error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
