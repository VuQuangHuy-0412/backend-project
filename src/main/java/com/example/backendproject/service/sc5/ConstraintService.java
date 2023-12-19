package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.entity.sc5.ConstraintEntity;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.mapper.ConstraintMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.repository.sc5.ClassRepository;
import com.example.backendproject.repository.sc5.ConstraintRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ConstraintService {
    private final ConstraintRepository constraintRepository;
    private final AdminLogService adminLogService;
    private final ConstraintMapper constraintMapper;

    public ConstraintService(ConstraintRepository constraintRepository,
                             AdminLogService adminLogService,
                             ConstraintMapper constraintMapper) {
        this.constraintRepository = constraintRepository;
        this.adminLogService = adminLogService;
        this.constraintMapper = constraintMapper;
    }

    public ConstraintSearchResponse searchConstraint(ConstraintSearchRequest request) {
        ConstraintSearchResponse response = new ConstraintSearchResponse();
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

//        List<Constraint> data = constraintRepository.searchConstraintByFilter(request);
//        response.setData(data);
        return response;
    }

    public void createConstraint(Constraint constraint) {
        adminLogService.log("createConstraint", CommonUtil.toJson(constraint));
        log.info("Create constraint with data: " + CommonUtil.toJson(constraint));

        validateCreateConstraintRequest(constraint);
        ConstraintEntity constraintEntity = constraintMapper.toEntity(constraint);
        try {
            constraintRepository.save(constraintEntity);
        } catch (Exception exception) {
            log.error("Save constraint error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateConstraintRequest(Constraint constraint) {

    }

    public void updateConstraint(Constraint constraint) {
        if (constraint.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy ràng buộc.");
        }
        validateCreateConstraintRequest(constraint);

        Optional<ConstraintEntity> constraintEntityOptional = constraintRepository.findById(constraint.getId());
        if (constraintEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy ràng buộc.");
        }

        ConstraintEntity constraintEntity = constraintEntityOptional.get();

        try {
            constraintRepository.save(constraintEntity);
        } catch (Exception exception) {
            log.error("Update constraint error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
