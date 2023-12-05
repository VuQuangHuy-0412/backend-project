package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.mapper.StudentProjectMapper;
import com.example.backendproject.model.sc5.StudentProject;
import com.example.backendproject.model.sc5.StudentProjectSearchRequest;
import com.example.backendproject.model.sc5.StudentProjectSearchResponse;
import com.example.backendproject.model.sc5.UploadStudentProjectRequest;
import com.example.backendproject.repository.sc5.StudentProjectRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StudentProjectService {
    private final StudentProjectRepository studentProjectRepository;
    private final AdminLogService adminLogService;
    private final StudentProjectMapper studentProjectMapper;

    public StudentProjectService(StudentProjectRepository studentProjectRepository,
                                 AdminLogService adminLogService,
                                 StudentProjectMapper studentProjectMapper) {
        this.studentProjectRepository = studentProjectRepository;
        this.adminLogService = adminLogService;
        this.studentProjectMapper = studentProjectMapper;
    }

    public StudentProjectSearchResponse searchStudentProject(StudentProjectSearchRequest request) {
        StudentProjectSearchResponse response = new StudentProjectSearchResponse();
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

        List<StudentProject> data = studentProjectRepository.searchStudentProjectByFilter(request);
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
        studentProjectEntity.setName(studentProject.getName());
        studentProjectEntity.setStudentCode(studentProject.getStudentCode());
        studentProjectEntity.setClassId(studentProject.getClassId());
        studentProjectEntity.setIsAssigned(studentProject.getIsAssigned());
        studentProjectEntity.setTeacher1Id(studentProject.getTeacher1Id());
        studentProjectEntity.setTeacher2Id(studentProject.getTeacher2Id());
        studentProjectEntity.setTeacher3Id(studentProject.getTeacher3Id());
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
        if (request == null || CollectionUtils.isEmpty(request.getStudentProjectCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (StudentProject studentProject : request.getStudentProjectCreateRequests()) {
            validateCreateStudentProjectRequest(studentProject);
        }

        List<StudentProjectEntity> entities = studentProjectMapper.toEntities(request.getStudentProjectCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        studentProjectRepository.saveAll(entities);
    }
}
