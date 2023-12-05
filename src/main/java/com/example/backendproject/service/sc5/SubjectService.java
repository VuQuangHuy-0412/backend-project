package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.mapper.SubjectMapper;
import com.example.backendproject.model.sc5.Subject;
import com.example.backendproject.model.sc5.SubjectSearchRequest;
import com.example.backendproject.model.sc5.SubjectSearchResponse;
import com.example.backendproject.repository.sc5.SubjectRepository;
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
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final AdminLogService adminLogService;
    private final SubjectMapper subjectMapper;

    public SubjectService(SubjectRepository subjectRepository,
                          AdminLogService adminLogService,
                          SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.adminLogService = adminLogService;
        this.subjectMapper = subjectMapper;
    }

    public SubjectSearchResponse searchSubject(SubjectSearchRequest request) {
        SubjectSearchResponse response = new SubjectSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        List<Subject> data = subjectRepository.searchSubjectByFilter(request);
        response.setData(data);
        return response;
    }

    public void createSubject(Subject subject) {
        adminLogService.log("createSubject", CommonUtil.toJson(subject));
        log.info("Create subject with data: " + CommonUtil.toJson(subject));

        validateCreateSubjectRequest(subject);
        SubjectEntity subjectEntity = subjectMapper.toEntity(subject);
        subjectEntity.setCreatedAt(new Date());
        subjectEntity.setUpdatedAt(new Date());
        try {
            subjectRepository.save(subjectEntity);
        } catch (Exception exception) {
            log.error("Save subject error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateSubjectRequest(Subject subject) {
        if (StringUtils.isBlank(subject.getName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên học phần.");
        }

        if (StringUtils.isBlank(subject.getCode())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin mã học phần.");
        }

        if (subject.getGroupId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin nhóm chuyên môn.");
        }
    }

    public void updateSubject(Subject subject) {
        if (subject.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }
        validateCreateSubjectRequest(subject);

        Optional<SubjectEntity> subjectEntityOptional = subjectRepository.findById(subject.getId());
        if (subjectEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }

        SubjectEntity subjectEntity = subjectEntityOptional.get();
        subjectEntity.setName(subject.getName());
        subjectEntity.setCode(subject.getCode());
        subjectEntity.setGroupId(subject.getGroupId());
        subjectEntity.setUpdatedAt(new Date());

        try {
            subjectRepository.save(subjectEntity);
        } catch (Exception exception) {
            log.error("Update subject error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
