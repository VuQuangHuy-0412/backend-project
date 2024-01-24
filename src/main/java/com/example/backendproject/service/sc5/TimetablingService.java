package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.sc5.EvaluateResponse;
import com.example.backendproject.model.sc5.TimetableResponse;
import com.example.backendproject.model.sc5.TimetableTeacher;
import com.example.backendproject.repository.sc5.*;
import com.example.backendproject.service.AdminLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    private final SubjectRepository subjectRepository;
    private final LanguageRepository languageRepository;

    public TimetablingService(TeacherRepository teacherRepository,
                              ClassRepository classRepository,
                              RequiredConstraintRepository requiredConstraintRepository,
                              CustomConstraintRepository customConstraintRepository,
                              AdminLogService adminLogService,
                              TeacherMapper teacherMapper,
                              ClassMapper classMapper,
                              TimetablingProcessRepository timetablingProcessRepository,
                              TimetablingServiceHelper timetablingServiceHelper, SubjectRepository subjectRepository, LanguageRepository languageRepository) {
        this.teacherRepository = teacherRepository;
        this.classRepository = classRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.customConstraintRepository = customConstraintRepository;
        this.adminLogService = adminLogService;
        this.teacherMapper = teacherMapper;
        this.classMapper = classMapper;
        this.timetablingProcessRepository = timetablingProcessRepository;
        this.timetablingServiceHelper = timetablingServiceHelper;
        this.subjectRepository = subjectRepository;
        this.languageRepository = languageRepository;
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

    public EvaluateResponse evaluateTimetablingTeacher(Long dataset) {
        EvaluateResponse response = new EvaluateResponse();
        if (dataset == null) {
            return response;
        }
        InputData inputData = timetablingServiceHelper.getInputData(dataset);

        int x1 = 0; // số lớp chưa được pc
        int x2 = 0; // số lớp chưa pc đúng chuyên môn hoặc ngôn ngữ
        int x3 = 0; // số GV dạy quá số giờ tối đa
        int x4 = 0; // số cặp lớp xung đột được pc chung 1 GV
        int x5 = 0; // số GV chưa được pc GD
        for (ClassEntity classEntity : inputData.getClasses()) {
            if (classEntity.getTeacherId() == null) {
                x1 += 1;
            } else {
                Optional<TeacherEntity> teacherEntity = teacherRepository.findById(classEntity.getTeacherId());
                if (teacherEntity.isPresent() && timetablingServiceHelper.isTeacherClassMapping(teacherEntity.get(), classEntity, inputData) == 0) {
                    x2 += 1;
                }

                for (ClassEntity classEntity1 : inputData.getClasses()) {
                    if (classEntity1.getTeacherId() != null && classEntity.getId() < classEntity1.getId() && classEntity.getTeacherId().equals(classEntity1.getTeacherId())) {
                        if (timetablingServiceHelper.isConflictClass(classEntity, classEntity1) == 1) {
                            x4 += 1;
                        }
                    }
                }
            }
        }

        for (TeacherEntity teacherEntity : inputData.getTeachers()) {
            List<ClassEntity> classes = classRepository.findByTeacherIdAndDataset(teacherEntity.getId(), dataset);
            if (CollectionUtils.isEmpty(classes)) {
                x5 += 1;
            } else {
                double totalTimeGD = classes.stream().map(ClassEntity::getTimeOfClass).reduce(0d, Double::sum);

                if (totalTimeGD > inputData.getAverageGD() * teacherEntity.getGdTime()) {
                    x3 += 1;
                }
            }
        }

        List<EvaluateResponse.EvaluateDetail> data = new ArrayList<>();
        data.add(new EvaluateResponse.EvaluateDetail("Số lớp chưa được phân công", String.valueOf(x1)));
        data.add(new EvaluateResponse.EvaluateDetail("Số lớp phân công chưa đáp ứng yêu cầu chuyên môn hoặc ngôn ngữ", String.valueOf(x2)));
        data.add(new EvaluateResponse.EvaluateDetail("Số giảng viên có số giờ được phân công lớn hơn số giờ GD tối đa", String.valueOf(x3)));
        data.add(new EvaluateResponse.EvaluateDetail("Số cặp lớp xung đột được phân công cho cùng GV", String.valueOf(x4)));
        data.add(new EvaluateResponse.EvaluateDetail("Số GV chưa được phân công GD", String.valueOf(x5)));
        response.setData(data);
        return response;
    }

    public TimetableResponse teacherTimetable(Long dataset, Long teacherId) {
        TimetableResponse response = new TimetableResponse();
        if (dataset == null || teacherId == null) {
            return response;
        }

        List<ClassEntity> classEntities = classRepository.findByTeacherIdAndDatasetOrderByDayOfWeekAscStartTimeAsc(teacherId, dataset);
        List<TimetableResponse.TimetableDetail> data = new ArrayList<>();
        if (CollectionUtils.isEmpty(classEntities)) {
            response.setData(data);
            return response;
        }

        for (ClassEntity classEntity : classEntities) {
            if (classEntity.getSubjectId() != null && classEntity.getLanguageId() != null) {
                Optional<SubjectEntity> subjectEntity = subjectRepository.findById(classEntity.getSubjectId());
                Optional<LanguageEntity> languageEntity = languageRepository.findById(classEntity.getLanguageId());
                data.add(new TimetableResponse.TimetableDetail(classEntity.getDayOfWeek(),
                        classEntity.getStartTime() + "-" + classEntity.getEndTime(),
                        subjectEntity.isEmpty() ? classEntity.getName() : subjectEntity.get().getName(),
                        classEntity.getCode(), classEntity.getBuilding() + "-" + classEntity.getRoom(), classEntity.getWeek(),
                        classEntity.getProgram(), languageEntity.isEmpty() ? "" : languageEntity.get().getName()));
            }
        }

        response.setData(data);
        return response;
    }
}
