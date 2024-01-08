package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.CustomConstraintEntity;
import com.example.backendproject.entity.sc5.RequiredConstraintEntity;
import com.example.backendproject.mapper.CustomConstraintMapper;
import com.example.backendproject.mapper.RequiredConstraintMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.CustomConstraintRepository;
import com.example.backendproject.repository.sc5.RequiredConstraintRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ConstraintService {
    private final CustomConstraintRepository customConstraintRepository;
    private final RequiredConstraintRepository requiredConstraintRepository;
    private final AdminLogService adminLogService;
    private final CustomConstraintMapper customConstraintMapper;
    private final RequiredConstraintMapper requiredConstraintMapper;

    public ConstraintService(CustomConstraintRepository customConstraintRepository,
                             RequiredConstraintRepository requiredConstraintRepository,
                             AdminLogService adminLogService,
                             CustomConstraintMapper customConstraintMapper,
                             RequiredConstraintMapper requiredConstraintMapper) {
        this.customConstraintRepository = customConstraintRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.adminLogService = adminLogService;
        this.customConstraintMapper = customConstraintMapper;
        this.requiredConstraintMapper = requiredConstraintMapper;
    }

    public ConstraintSearchResponse searchConstraint(ConstraintSearchRequest request) {
        ConstraintSearchResponse response = new ConstraintSearchResponse();

        List<ObjectiveFunction> objectiveFunctions = new ArrayList<>();
        ObjectiveFunction objectiveFunction1 = new ObjectiveFunction();
        objectiveFunction1.setCode("OF1");
        objectiveFunction1.setValue("Mức độ chênh lệch lớn nhất giữa tổng số giờ giảng dạy của 1 giảng viên và tích giữa số giờ giảng dạy tối đa của giảng viên với tỉ lệ giờ giảng dạy trung bình là nhỏ nhất");
        objectiveFunction1.setStatus(1);
        objectiveFunction1.setType("teacher");
        objectiveFunctions.add(objectiveFunction1);
        ObjectiveFunction objectiveFunction2 = new ObjectiveFunction();
        objectiveFunction2.setCode("OF2");
        objectiveFunction2.setValue("Mức độ chênh lệch lớn nhất giữa tổng số giờ hướng dẫn của 1 giảng viên và tích giữa số giờ hướng dẫn tối đa của giảng viên với tỉ lệ giờ hướng dẫn trung bình là nhỏ nhất");
        objectiveFunction2.setStatus(1);
        objectiveFunction2.setType("student");
        objectiveFunctions.add(objectiveFunction2);
        response.setObjectiveFunctions(objectiveFunctions);

        List<CustomConstraint> customConstraints = customConstraintRepository.searchCustomConstraintByFilter(request);
        response.setCustomConstraints(customConstraints);

        List<RequiredConstraint> requiredConstraints = requiredConstraintRepository.searchRequiredConstraintByFilter(request);
        response.setRequiredConstraints(requiredConstraints);

        return response;
    }

    public void createCustomConstraint(CustomConstraint customConstraint) {
        adminLogService.log("createCustomConstraint", CommonUtil.toJson(customConstraint));
        log.info("Create custom constraint with data: " + CommonUtil.toJson(customConstraint));

        CustomConstraintEntity customConstraintEntity = customConstraintMapper.toEntity(customConstraint);
        customConstraintEntity.setCreatedAt(new Date());
        customConstraintEntity.setUpdatedAt(new Date());
        try {
            customConstraintRepository.save(customConstraintEntity);
        } catch (Exception exception) {
            log.error("Save custom constraint error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateCustomConstraint(CustomConstraint customConstraint) {
        if (customConstraint.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy ràng buộc.");
        }

        Optional<CustomConstraintEntity> constraintEntityOptional = customConstraintRepository.findById(customConstraint.getId());
        if (constraintEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy ràng buộc.");
        }

        CustomConstraintEntity customConstraintEntity = constraintEntityOptional.get();
        customConstraintEntity.setTeacherColumnCompare(customConstraint.getTeacherColumnCompare());
        customConstraintEntity.setTeacherCompare(customConstraint.getTeacherCompare());
        customConstraintEntity.setTeacherValueCompare(customConstraintEntity.getTeacherValueCompare());
        customConstraintEntity.setClassCompare(customConstraint.getClassCompare());
        customConstraintEntity.setClassValueCompare(customConstraint.getClassValueCompare());
        customConstraintEntity.setClassColumnCompare(customConstraint.getClassColumnCompare());
        customConstraintEntity.setStatus(customConstraint.getStatus());
        customConstraintEntity.setDataset(customConstraint.getDataset());
        customConstraintEntity.setUpdatedAt(new Date());

        try {
            customConstraintRepository.save(customConstraintEntity);
        } catch (Exception exception) {
            log.error("Update custom constraint error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void createRequiredConstraint(RequiredConstraint requiredConstraint) {
        adminLogService.log("createRequiredConstraint", CommonUtil.toJson(requiredConstraint));
        log.info("Create required constraint with data: " + CommonUtil.toJson(requiredConstraint));

        RequiredConstraintEntity requiredConstraintEntity = requiredConstraintMapper.toEntity(requiredConstraint);
        requiredConstraintEntity.setCreatedAt(new Date());
        requiredConstraintEntity.setUpdatedAt(new Date());
        try {
            requiredConstraintRepository.save(requiredConstraintEntity);
        } catch (Exception exception) {
            log.error("Save required constraint error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateRequiredConstraint(RequiredConstraint requiredConstraint) {
        if (requiredConstraint.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy ràng buộc.");
        }

        Optional<RequiredConstraintEntity> requiredConstraintEntityOptional = requiredConstraintRepository.findById(requiredConstraint.getId());
        if (requiredConstraintEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy ràng buộc.");
        }

        RequiredConstraintEntity requiredConstraintEntity = requiredConstraintEntityOptional.get();
        requiredConstraintEntity.setCode(requiredConstraint.getCode());
        requiredConstraintEntity.setValue(requiredConstraint.getValue());
        requiredConstraintEntity.setStatus(requiredConstraint.getStatus());
        requiredConstraintEntity.setType(requiredConstraint.getType());
        requiredConstraintEntity.setDataset(requiredConstraint.getDataset());
        requiredConstraintEntity.setUpdatedAt(new Date());

        try {
            requiredConstraintRepository.save(requiredConstraintEntity);
        } catch (Exception exception) {
            log.error("Update required constraint error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
