package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.mapper.StudentProjectMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.sc5.EvaluateResponse;
import com.example.backendproject.model.sc5.TimetableStudent;
import com.example.backendproject.repository.sc5.*;
import com.example.backendproject.service.AdminLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void timetablingStudent(Long dataset) throws JsonProcessingException {
        adminLogService.log("timetablingStudent", null);
        TimetablingProcessEntity entity = timetablingProcessRepository.findByTypeAndDataset("student", dataset);
        if (entity != null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Đang trong quá trình thực hiện phân công HD");
        }

        entity = new TimetablingProcessEntity();
        entity.setType("student");
        entity.setStatus("INIT");
        entity.setDataset(dataset);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        entity = timetablingProcessRepository.save(entity);

        timeTablingStudentServiceHelper.timetablingStudentAsync(entity);
    }

    public TimetableStudent getTimeTableOfStudent(Long teacherId, Long dataset) {
        if (teacherId == null || teacherId <= 0) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Mã giảng viên không hợp lệ");
        }

        Optional<TeacherEntity> teacherEntity = teacherRepository.findById(teacherId);
        if (teacherEntity.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin giảng viên");
        }

        TimetableStudent timetableStudent = new TimetableStudent();
        timetableStudent.setTeacher(teacherMapper.toDto(teacherEntity.get()));
        List<StudentProjectEntity> studentProjectEntities = studentProjectRepository.findByTeacherAssignedIdAndDataset(teacherId, dataset);
        timetableStudent.setData(studentProjectMapper.toDtos(studentProjectEntities));
        return timetableStudent;
    }

    public InputData getTimetablingStudentInputData(Long dataset) {
        InputData inputData = new InputData();
        List<TeacherEntity> teachers = teacherRepository.findAllByStatusAndDataset(TeacherConstant.Status.ACTIVE, dataset);
        List<StudentProjectEntity> studentProjectEntities = studentProjectRepository.findByDataset(dataset);
        List<RequiredConstraintEntity> requiredConstraints = requiredConstraintRepository.findByStatusAndDataset(1, dataset);
        List<CustomConstraintEntity> customConstraints = customConstraintRepository.findByStatusAndDataset(1, dataset);

        inputData.setTeachers(teachers);
        inputData.setStudentProjects(studentProjectEntities);
        inputData.setRequiredConstraints(requiredConstraints);
        inputData.setCustomConstraints(customConstraints);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfStudents(studentProjectEntities.size());

        return inputData;
    }

    public EvaluateResponse evaluateTimetablingStudent(Long dataset) {
        EvaluateResponse response = new EvaluateResponse();
        InputData inputData = timeTablingStudentServiceHelper.getInputData(dataset);

        int x1 = 0; // số SV chưa được pc
        int x2 = 0; // số SV chưa pc đúng nguyện vọng
        int x3 = 0; // số GV dạy quá số giờ tối đa
        for (StudentProjectEntity studentProjectEntity : inputData.getStudentProjects()) {
            if (studentProjectEntity.getTeacherAssignedId() == null) {
                x1 += 1;
            } else {
                Optional<TeacherEntity> teacherEntity = teacherRepository.findById(studentProjectEntity.getTeacherAssignedId());
                if (teacherEntity.isPresent() && studentProjectEntity.getTeacher1Id() != null &&
                        studentProjectEntity.getTeacher2Id() != null && studentProjectEntity.getTeacher3Id() != null &&
                        !teacherEntity.get().getId().equals(studentProjectEntity.getTeacher1Id()) &&
                        !teacherEntity.get().getId().equals(studentProjectEntity.getTeacher2Id()) &&
                        !teacherEntity.get().getId().equals(studentProjectEntity.getTeacher3Id())) {
                    x2 += 1;
                }
            }
        }

        for (TeacherEntity teacherEntity : inputData.getTeachers()) {
            List<StudentProjectEntity> studentProjectEntities = studentProjectRepository.findByTeacherAssignedIdAndDataset(teacherEntity.getId(), dataset);
            double totalTimeHD = studentProjectEntities.stream().map(StudentProjectEntity::getTimeHd).reduce(0d, Double::sum);

            if (totalTimeHD > inputData.getAverageHD() * teacherEntity.getHdTime()) {
                x3 += 1;
            }
        }

        List<EvaluateResponse.EvaluateDetail> data = new ArrayList<>();
        data.add(new EvaluateResponse.EvaluateDetail("Số SV chưa được phân công", String.valueOf(x1)));
        data.add(new EvaluateResponse.EvaluateDetail("Số SV được phân công chưa đáp ứng nguyện vọng đăng ký", String.valueOf(x2)));
        data.add(new EvaluateResponse.EvaluateDetail("Số giảng viên có số giờ được phân công lớn hơn số giờ HD tối đa", String.valueOf(x3)));
        response.setData(data);
        return response;
    }
}
