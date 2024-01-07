package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.mapper.SubjectMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.repository.sc5.ClassRepository;
import com.example.backendproject.repository.sc5.SubjectRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.service.sc5.helper.ClassServiceHelper;
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
public class ClassService {
    private final ClassRepository classRepository;
    private final AdminLogService adminLogService;
    private final ClassMapper classMapper;
    private final ClassServiceHelper classServiceHelper;
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    public ClassService(ClassRepository classRepository,
                        AdminLogService adminLogService,
                        ClassMapper classMapper,
                        ClassServiceHelper classServiceHelper,
                        SubjectRepository subjectRepository,
                        SubjectMapper subjectMapper,
                        TeacherRepository teacherRepository,
                        TeacherMapper teacherMapper) {
        this.classRepository = classRepository;
        this.adminLogService = adminLogService;
        this.classMapper = classMapper;
        this.classServiceHelper = classServiceHelper;
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    public ClassSearchResponse searchClass(ClassSearchRequest request) {
        ClassSearchResponse response = new ClassSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        List<Class> data = classRepository.searchClassByFilter(request);
        for (Class classDto : data) {
            if (classDto.getSubjectId() != null) {
                Optional<SubjectEntity> subjectEntity = subjectRepository.findById(classDto.getSubjectId());
                subjectEntity.ifPresent(entity -> classDto.setSubject(subjectMapper.toDto(entity)));
            }
            if (classDto.getTeacherId() != null) {
                Optional<TeacherEntity> teacherEntity = teacherRepository.findById(classDto.getTeacherId());
                teacherEntity.ifPresent(entity -> classDto.setTeacher(teacherMapper.toDto(entity)));
            }
        }
        response.setData(data);
        return response;
    }

    public void createClass(Class classDto) {
        adminLogService.log("createClass", CommonUtil.toJson(classDto));
        log.info("Create class with data: " + CommonUtil.toJson(classDto));

        validateCreateClassRequest(classDto);
        ClassEntity classEntity = classMapper.toEntity(classDto);
        classEntity.setCreatedAt(new Date());
        classEntity.setUpdatedAt(new Date());
        try {
            classRepository.save(classEntity);
        } catch (Exception exception) {
            log.error("Save class error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateClassRequest(Class classDto) {
    }

    public void updateClass(Class classDto) {
        if (classDto.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }
        validateCreateClassRequest(classDto);

        Optional<ClassEntity> classEntityOptional = classRepository.findById(classDto.getId());
        if (classEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }

        ClassEntity classEntity = classEntityOptional.get();
        if (!StringUtils.isBlank(classDto.getName()) && !StringUtils.isBlank(classDto.getCode())) {
            classEntity.setName(classDto.getName());
            classEntity.setCode(classDto.getCode());
            classEntity.setSemester(classDto.getSemester());
            classEntity.setSubjectId(classDto.getSubjectId());
            classEntity.setWeek(classDto.getWeek());
            classEntity.setDayOfWeek(classDto.getDayOfWeek());
            classEntity.setTimeOfDay(classDto.getTimeOfDay());
            classEntity.setTimeOfClass(classDto.getTimeOfClass());
            classEntity.setLanguageId(classDto.getLanguageId());
            classEntity.setIsAssigned(classDto.getIsAssigned());
            classEntity.setBuilding(classDto.getBuilding());
            classEntity.setRoom(classDto.getRoom());
            classEntity.setStartTime(classDto.getStartTime());
            classEntity.setEndTime(classDto.getEndTime());
            classEntity.setClassType(classDto.getClassType());
            classEntity.setNumberOfStudent(classDto.getNumberOfStudent());
            classEntity.setNumberOfCredits(classDto.getNumberOfCredits());
            classEntity.setProgram(classDto.getProgram());
        }
        classEntity.setTeacherId(classDto.getTeacherId());
        classEntity.setUpdatedAt(new Date());

        try {
            classRepository.save(classEntity);
        } catch (Exception exception) {
            log.error("Update class error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileClass(UploadClassRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getClassCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (ClassUpload classUpload : request.getClassCreateRequests()) {
            validateCreateClassRequestUpload(classUpload);
        }

        classServiceHelper.uploadFileClass(request);
    }

    private void validateCreateClassRequestUpload(ClassUpload classUpload) {
    }
}
