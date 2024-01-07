package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.sc5.TimetableTeacher;
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
public class TimetablingService {
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final RequiredConstraintRepository requiredConstraintRepository;
    private final CustomConstraintRepository customConstraintRepository;
    private final AdminLogService adminLogService;
    private final TeacherMapper teacherMapper;
    private final ClassMapper classMapper;
    private final TimetablingProcessRepository timetablingProcessRepository;
    private final TimetablingServiceHelper timetablingServiceHelper;

    public TimetablingService(TeacherRepository teacherRepository,
                              ClassRepository classRepository,
                              RequiredConstraintRepository requiredConstraintRepository,
                              CustomConstraintRepository customConstraintRepository,
                              AdminLogService adminLogService,
                              TeacherMapper teacherMapper,
                              ClassMapper classMapper,
                              TimetablingProcessRepository timetablingProcessRepository,
                              TimetablingServiceHelper timetablingServiceHelper) {
        this.teacherRepository = teacherRepository;
        this.classRepository = classRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.customConstraintRepository = customConstraintRepository;
        this.adminLogService = adminLogService;
        this.teacherMapper = teacherMapper;
        this.classMapper = classMapper;
        this.timetablingProcessRepository = timetablingProcessRepository;
        this.timetablingServiceHelper = timetablingServiceHelper;
    }

    public void timetablingTeacher(Long dataset) throws JsonProcessingException {
        adminLogService.log("timetablingTeacher", null);
        TimetablingProcessEntity entity = timetablingProcessRepository.findByTypeAndDataset("teacher", dataset);
        if (entity != null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Đang trong quá trình thực hiện phân công GD");
        }

        entity = new TimetablingProcessEntity();
        entity.setType("teacher");
        entity.setStatus("INIT");
        entity.setDataset(dataset);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        entity = timetablingProcessRepository.save(entity);

        timetablingServiceHelper.timetablingTeacherAsync(entity);
    }

    public TimetableTeacher getTimeTableOfTeacher(Long teacherId, Long dataset) {
        if (teacherId == null || teacherId <= 0) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mã giảng viên không hợp lệ");
        }

        Optional<TeacherEntity> teacherEntity = teacherRepository.findById(teacherId);
        if (teacherEntity.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin giảng viên");
        }

        TimetableTeacher timetableTeacher = new TimetableTeacher();
        timetableTeacher.setTeacher(teacherMapper.toDto(teacherEntity.get()));
        List<ClassEntity> classEntities = classRepository.findByTeacherIdAndDataset(teacherId, dataset);
        timetableTeacher.setData(classMapper.toDtos(classEntities));
        return timetableTeacher;
    }

    public InputData getTimetablingTeacherInputData(Long dataset) {
        InputData inputData = new InputData();
        List<TeacherEntity> teachers = teacherRepository.findAllByStatusAndDataset(TeacherConstant.Status.ACTIVE, dataset);
        List<ClassEntity> classes = classRepository.findByDataset(dataset);
        List<RequiredConstraintEntity> requiredConstraints = requiredConstraintRepository.findByStatusAndDataset(1, dataset);
        List<CustomConstraintEntity> customConstraints = customConstraintRepository.findByStatusAndDataset(1, dataset);

        inputData.setTeachers(teachers);
        inputData.setClasses(classes);
        inputData.setRequiredConstraints(requiredConstraints);
        inputData.setCustomConstraints(customConstraints);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfClasses(classes.size());

        return inputData;
    }

    public TimetablingProcessEntity getTimetablingTeacherStatus(Long dataset) {
        return timetablingProcessRepository.findByTypeAndDataset("teacher", dataset);
    }

    public TimetablingProcessEntity getTimetablingStudentStatus(Long dataset) {
        return timetablingProcessRepository.findByTypeAndDataset("student", dataset);
    }

}
