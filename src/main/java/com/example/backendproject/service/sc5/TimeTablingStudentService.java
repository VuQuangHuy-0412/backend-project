package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.mapper.StudentProjectMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.sc5.TimetableStudent;
import com.example.backendproject.repository.sc5.*;
import com.example.backendproject.service.AdminLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TimeTablingStudentService {
    private final TeacherRepository teacherRepository;
    private final StudentProjectRepository studentProjectRepository;
    private final RequiredConstraintRepository requiredConstraintRepository;
    private final CustomConstraintRepository customConstraintRepository;
    private final AdminLogService adminLogService;
    private final TeacherMapper teacherMapper;
    private final StudentProjectMapper studentProjectMapper;
    private final TimetablingProcessRepository timetablingProcessRepository;
    private final TimeTablingStudentServiceHelper timeTablingStudentServiceHelper;

    public TimeTablingStudentService(TeacherRepository teacherRepository,
                                     StudentProjectRepository studentProjectRepository,
                                     RequiredConstraintRepository requiredConstraintRepository,
                                     CustomConstraintRepository customConstraintRepository,
                                     AdminLogService adminLogService,
                                     TeacherMapper teacherMapper,
                                     StudentProjectMapper studentProjectMapper,
                                     TimetablingProcessRepository timetablingProcessRepository,
                                     TimeTablingStudentServiceHelper timeTablingStudentServiceHelper) {
        this.teacherRepository = teacherRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.customConstraintRepository = customConstraintRepository;
        this.adminLogService = adminLogService;
        this.teacherMapper = teacherMapper;
        this.studentProjectMapper = studentProjectMapper;
        this.timetablingProcessRepository = timetablingProcessRepository;
        this.timeTablingStudentServiceHelper = timeTablingStudentServiceHelper;
    }

    public void timetablingStudent() throws JsonProcessingException {
        adminLogService.log("timetablingStudent", null);
        TimetablingProcessEntity entity = timetablingProcessRepository.findByType("student");
        if (entity != null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Đang trong quá trình thực hiện phân công HD");
        }

        entity = new TimetablingProcessEntity();
        entity.setType("student");
        entity.setStatus("INIT");
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        entity = timetablingProcessRepository.save(entity);

        timeTablingStudentServiceHelper.timetablingStudentAsync(entity);
    }

    public TimetableStudent getTimeTableOfStudent(Long teacherId) {
        if (teacherId == null || teacherId <= 0) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mã giảng viên không hợp lệ");
        }

        Optional<TeacherEntity> teacherEntity = teacherRepository.findById(teacherId);
        if (teacherEntity.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin giảng viên");
        }

        TimetableStudent timetableStudent = new TimetableStudent();
        timetableStudent.setTeacher(teacherMapper.toDto(teacherEntity.get()));
        List<StudentProjectEntity> studentProjectEntities = studentProjectRepository.findByTeacherAssignedId(teacherId);
        timetableStudent.setData(studentProjectMapper.toDtos(studentProjectEntities));
        return timetableStudent;
    }

    public InputData getTimetablingStudentInputData() {
        InputData inputData = new InputData();
        List<TeacherEntity> teachers = teacherRepository.findAllByStatus(TeacherConstant.Status.ACTIVE);
        List<StudentProjectEntity> studentProjectEntities = studentProjectRepository.findAll();
        List<RequiredConstraintEntity> requiredConstraints = requiredConstraintRepository.findAll();
        List<CustomConstraintEntity> customConstraints = customConstraintRepository.findAll();

        inputData.setTeachers(teachers);
        inputData.setStudentProjects(studentProjectEntities);
        inputData.setRequiredConstraints(requiredConstraints);
        inputData.setCustomConstraints(customConstraints);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfStudents(studentProjectEntities.size());

        return inputData;
    }
}
