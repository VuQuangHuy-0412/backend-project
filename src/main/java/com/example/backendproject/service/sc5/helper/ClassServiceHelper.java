package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.entity.sc5.LanguageEntity;
import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.model.sc5.ClassUpload;
import com.example.backendproject.model.sc5.UploadClassRequest;
import com.example.backendproject.repository.sc5.ClassRepository;
import com.example.backendproject.repository.sc5.LanguageRepository;
import com.example.backendproject.repository.sc5.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ClassServiceHelper {
    private final ClassMapper classMapper;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final LanguageRepository languageRepository;

    public ClassServiceHelper(ClassMapper classMapper,
                              ClassRepository classRepository,
                              SubjectRepository subjectRepository,
                              LanguageRepository languageRepository) {
        this.classMapper = classMapper;
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
        this.languageRepository = languageRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileClass(UploadClassRequest request) {
        for (ClassUpload classDto : request.getClassCreateRequests()) {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setName(classDto.getName());
            classEntity.setCode(classDto.getCode());
            classEntity.setSemester(classDto.getSemester());
            classEntity.setWeek(classDto.getWeek());
            classEntity.setDayOfWeek(classDto.getDayOfWeek());
            classEntity.setBuilding(classDto.getBuilding());
            classEntity.setRoom(classDto.getRoom());
            classEntity.setStartTime(classDto.getStartTime());
            classEntity.setEndTime(classDto.getEndTime());
            classEntity.setClassType(classDto.getClassType());
            classEntity.setNumberOfStudent(classDto.getNumberOfStudent());
            classEntity.setNumberOfCredits(classDto.getNumberOfCredits());
            classEntity.setProgram(classDto.getProgram());
            classEntity.setDataset(request.getDataset());

            List<SubjectEntity> subjectEntity = subjectRepository.findByCodeAndDataset(classDto.getSubjectCode(), request.getDataset());
            classEntity.setSubjectId(CollectionUtils.isEmpty(subjectEntity) ? 1L : subjectEntity.get(0).getId());

            Double timeOfClass = getTimeOfClass(classDto);
            classEntity.setTimeOfClass(timeOfClass);
            List<LanguageEntity> languageEntity = languageRepository.findByName(classDto.getLanguageName());
            classEntity.setLanguageId(CollectionUtils.isEmpty(languageEntity) ? 1L : languageEntity.get(0).getId());
            classEntity.setCreatedAt(new Date());
            classEntity.setUpdatedAt(new Date());
            classRepository.save(classEntity);
        }
    }

    private Double getTimeOfClass(ClassUpload classUpload) {
        try {
            int tl = getTl(classUpload);
            int semester = Integer.parseInt(classUpload.getSemester());
            String classType = classUpload.getClassType();
            String program = classUpload.getProgram();
            double kc = getKc(semester, classType, program);
            double kl = getKl(classUpload.getNumberOfStudent());

            return (double) tl * (kc + kl);
        } catch (Exception ex) {
            return 0d;
        }
    }

    private int getTl(ClassUpload classUpload) {
        int startTime = classUpload.getStartTime();
        int endTime = classUpload.getEndTime();

        int diffTime = endTime / 100 * 60 + endTime % 100 - (startTime / 100 * 60 + startTime % 100);
        return diffTime / 50;
    }

    private double getKc(int semester, String classType, String program) {
        List<String> LTBT = List.of("LT", "BT", "LT+BT");
        List<String> THTN = List.of("TH", "TN");
        if ("CT CHUẨN".equals(program) && semester % 10 != 3  && LTBT.contains(classType)) {
            return 1.5;
        }
        if ("CT CHUẨN".equals(program) && semester % 10 != 3  && THTN.contains(classType)) {
            return 1.0;
        }
        if ("CT CHUẨN".equals(program) && semester % 10 == 3  && LTBT.contains(classType)) {
            return 2.5;
        }
        if ("CT CHUẨN".equals(program) && semester % 10 == 3  && THTN.contains(classType)) {
            return 1.5;
        }
        if ("ELITECH".equals(program)  && THTN.contains(classType)) {
            return 1.5;
        }
        if ("ELITECH".equals(program)  && LTBT.contains(classType)) {
            return 1.8;
        }
        if ("SIE".equals(program)  && LTBT.contains(classType)) {
            return 2.0;
        }
        if ("SIE".equals(program)  && THTN.contains(classType)) {
            return 1.5;
        }
        return 0d;
    }

    private double getKl(Integer numOfStudents) {
        if (numOfStudents == null) {
            return 0d;
        }
        if (numOfStudents <= 60) {
            return 0d;
        }
        if (numOfStudents <= 120) {
            return 0.2;
        }
        if (numOfStudents <= 180) {
            return 0.4;
        }
        if (numOfStudents <= 240) {
            return 0.6;
        }
        if (numOfStudents <= 300) {
            return 0.8;
        }
        return 1.0;
    }
}
