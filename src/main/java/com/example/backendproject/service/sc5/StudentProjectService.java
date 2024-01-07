package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.StudentProjectMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.repository.sc5.StudentProjectRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.service.sc5.helper.StudentProjectServiceHelper;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StudentProjectService {
    private final StudentProjectRepository studentProjectRepository;
    private final AdminLogService adminLogService;
    private final StudentProjectMapper studentProjectMapper;
    private final StudentProjectServiceHelper studentProjectServiceHelper;
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    public StudentProjectService(StudentProjectRepository studentProjectRepository,
                                 AdminLogService adminLogService,
                                 StudentProjectMapper studentProjectMapper,
                                 StudentProjectServiceHelper studentProjectServiceHelper,
                                 TeacherRepository teacherRepository,
                                 TeacherMapper teacherMapper) {
        this.studentProjectRepository = studentProjectRepository;
        this.adminLogService = adminLogService;
        this.studentProjectMapper = studentProjectMapper;
        this.studentProjectServiceHelper = studentProjectServiceHelper;
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    public StudentProjectSearchResponse searchStudentProject(StudentProjectSearchRequest request) {
        StudentProjectSearchResponse response = new StudentProjectSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        if (request.getDataset() == null) {
            response.setData(new ArrayList<>());
            return response;
        }

        List<StudentProject> data = studentProjectRepository.searchStudentProjectByFilter(request);
        for (StudentProject studentProject : data) {
            if (studentProject.getTeacher1Id() != null) {
                Optional<TeacherEntity> teacher1Entity = teacherRepository.findById(studentProject.getTeacher1Id());
                teacher1Entity.ifPresent(entity -> studentProject.setTeacher1(teacherMapper.toDto(entity)));
            }
            if (studentProject.getTeacher2Id() != null) {
                Optional<TeacherEntity> teacher2Entity = teacherRepository.findById(studentProject.getTeacher2Id());
                teacher2Entity.ifPresent(entity -> studentProject.setTeacher2(teacherMapper.toDto(entity)));
            }
            if (studentProject.getTeacher3Id() != null) {
                Optional<TeacherEntity> teacher3Entity = teacherRepository.findById(studentProject.getTeacher3Id());
                teacher3Entity.ifPresent(entity -> studentProject.setTeacher3(teacherMapper.toDto(entity)));
            }
            if (studentProject.getTeacherAssignedId() != null) {
                Optional<TeacherEntity> teacherAssignedEntity = teacherRepository.findById(studentProject.getTeacherAssignedId());
                teacherAssignedEntity.ifPresent(entity -> studentProject.setTeacherAssigned(teacherMapper.toDto(entity)));
            }
        }
        response.setData(data);
        return response;
    }

    public void createStudentProject(StudentProject studentProject) {
        adminLogService.log("createStudentProject", CommonUtil.toJson(studentProject));
        log.info("Create studentProject with data: " + CommonUtil.toJson(studentProject));

        validateCreateStudentProjectRequest(studentProject);
        StudentProjectEntity studentProjectEntity = studentProjectMapper.toEntity(studentProject);
        studentProjectEntity.setCreatedAt(new Date());
        studentProjectEntity.setUpdatedAt(new Date());
        try {
            studentProjectRepository.save(studentProjectEntity);
        } catch (Exception exception) {
            log.error("Save studentProject error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateStudentProjectRequest(StudentProject studentProject) {

    }

    public void updateStudentProject(StudentProject studentProject) {
        if (studentProject.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }
        validateCreateStudentProjectRequest(studentProject);

        Optional<StudentProjectEntity> studentProjectEntityOptional = studentProjectRepository.findById(studentProject.getId());
        if (studentProjectEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }

        StudentProjectEntity studentProjectEntity = studentProjectEntityOptional.get();
        if (!StringUtils.isBlank(studentProject.getName()) && !StringUtils.isBlank(studentProject.getStudentCode())) {
            studentProjectEntity.setName(studentProject.getName());
            studentProjectEntity.setStudentCode(studentProject.getStudentCode());
            studentProjectEntity.setTimeHd(studentProject.getTimeHd());
            studentProjectEntity.setIsAssigned(studentProject.getIsAssigned());
            studentProjectEntity.setTeacher1Id(studentProject.getTeacher1Id());
            studentProjectEntity.setTeacher2Id(studentProject.getTeacher2Id());
            studentProjectEntity.setTeacher3Id(studentProject.getTeacher3Id());
            studentProjectEntity.setProjectType(studentProject.getProjectType());
            studentProjectEntity.setProjectName(studentProject.getProjectName());
            studentProjectEntity.setClassId(studentProject.getClassId());
            studentProjectEntity.setDataset(studentProject.getDataset());
        }
        studentProjectEntity.setTeacherAssignedId(studentProject.getTeacherAssignedId());
        studentProjectEntity.setUpdatedAt(new Date());

        try {
            studentProjectRepository.save(studentProjectEntity);
        } catch (Exception exception) {
            log.error("Update studentProject error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileStudentProject(UploadStudentProjectRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getStudentProjectCreateRequests()) || request.getDataset() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (StudentProjectUpload studentProject : request.getStudentProjectCreateRequests()) {
            validateCreateStudentProjectRequestUpload(studentProject);
        }

        studentProjectServiceHelper.uploadFileStudentProject(request);
    }

    private void validateCreateStudentProjectRequestUpload(StudentProjectUpload studentProject) {

    }
}
