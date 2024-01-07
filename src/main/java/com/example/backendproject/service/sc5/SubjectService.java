package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.SubjectMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import com.example.backendproject.repository.sc5.SubjectRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.service.sc5.helper.SubjectServiceHelper;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final AdminLogService adminLogService;
    private final SubjectMapper subjectMapper;
    private final SubjectServiceHelper subjectServiceHelper;
    private final GroupTeacherRepository groupTeacherRepository;
    private final GroupTeacherMapper groupTeacherMapper;

    public SubjectService(SubjectRepository subjectRepository,
                          AdminLogService adminLogService,
                          SubjectMapper subjectMapper,
                          SubjectServiceHelper subjectServiceHelper,
                          GroupTeacherRepository groupTeacherRepository,
                          GroupTeacherMapper groupTeacherMapper) {
        this.subjectRepository = subjectRepository;
        this.adminLogService = adminLogService;
        this.subjectMapper = subjectMapper;
        this.subjectServiceHelper = subjectServiceHelper;
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMapper = groupTeacherMapper;
    }

    public SubjectSearchResponse searchSubject(SubjectSearchRequest request) {
        SubjectSearchResponse response = new SubjectSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        List<Subject> data = subjectRepository.searchSubjectByFilter(request);
        for (Subject subject : data) {
            if (subject.getGroupId() != null) {
                Optional<GroupTeacherEntity> groupTeacherEntity = groupTeacherRepository.findById(subject.getGroupId());
                groupTeacherEntity.ifPresent(entity -> subject.setGroupTeacher(groupTeacherMapper.toDto(entity)));
            }
        }
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
        subjectEntity.setDataset(subject.getDataset());
        subjectEntity.setUpdatedAt(new Date());

        try {
            subjectRepository.save(subjectEntity);
        } catch (Exception exception) {
            log.error("Update subject error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileSubject(UploadSubjectRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getSubjectCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (SubjectUpload subject : request.getSubjectCreateRequests()) {
            validateCreateSubjectRequestUpload(subject);
        }

        subjectServiceHelper.uploadFileSubject(request);
    }

    private void validateCreateSubjectRequestUpload(SubjectUpload subject) {
        if (StringUtils.isBlank(subject.getName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên học phần.");
        }

        if (StringUtils.isBlank(subject.getCode())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin mã học phần.");
        }

        if (subject.getGroupName() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin nhóm chuyên môn.");
        }
    }
}
