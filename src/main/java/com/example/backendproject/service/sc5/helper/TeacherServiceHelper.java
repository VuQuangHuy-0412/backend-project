package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.LanguageEntity;
import com.example.backendproject.entity.sc5.LanguageTeacherMappingEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.LanguageTeacherMappingMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.LanguageRepository;
import com.example.backendproject.repository.sc5.LanguageTeacherMappingRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TeacherServiceHelper {
    private final TeacherMapper teacherMapper;
    private final TeacherRepository teacherRepository;
    private final LanguageTeacherMappingMapper languageTeacherMappingMapper;
    private final LanguageTeacherMappingRepository languageTeacherMappingRepository;
    private final LanguageRepository languageRepository;

    public TeacherServiceHelper(TeacherMapper teacherMapper,
                                TeacherRepository teacherRepository,
                                LanguageTeacherMappingMapper languageTeacherMappingMapper,
                                LanguageTeacherMappingRepository languageTeacherMappingRepository,
                                LanguageRepository languageRepository) {
        this.teacherMapper = teacherMapper;
        this.teacherRepository = teacherRepository;
        this.languageTeacherMappingMapper = languageTeacherMappingMapper;
        this.languageTeacherMappingRepository = languageTeacherMappingRepository;
        this.languageRepository = languageRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileTeacher(UploadTeacherRequest request) {
        for (TeacherUpload teacher : request.getTeacherCreateRequests()) {
            TeacherEntity entity = new TeacherEntity();
            entity.setFullName(teacher.getFullName());
            entity.setRankAndDegree("GV");
            entity.setStartTime(new Date());
            entity.setBirthday(new Date());
            entity.setRating(5.0f);
            entity.setStatus(1);
            entity.setTotalTime(teacher.getTotalTime());
            entity.setDataset(request.getDataset());
            entity.setCreatedAt(new Date());
            entity.setUpdatedAt(new Date());
            teacherRepository.save(entity);
        }
    }

    @Async("async-thread-pool")
    public void uploadFileLanguageTeacherMapping(UploadLanguageTeacherRequest request) {
        for (LanguageTeacherMappingUpload languageTeacherMapping : request.getLanguageTeacherCreateRequests()) {
            try {
                List<TeacherEntity> teacherEntities = teacherRepository.findByFullNameAndDataset(languageTeacherMapping.getTeacherName(), request.getDataset());
                if (!CollectionUtils.isEmpty(teacherEntities)) {
                    Long teacherId = teacherEntities.get(0).getId();
                    List<LanguageEntity> languageEntities = languageRepository.findByName(languageTeacherMapping.getLanguageName());
                    Long languageId = CollectionUtils.isEmpty(languageEntities) ? 2L : languageEntities.get(0).getId();
                    List<LanguageTeacherMappingEntity> entity = languageTeacherMappingRepository.findByTeacherIdAndLanguageIdAndDataset(
                            teacherId, languageId, request.getDataset());
                    if (CollectionUtils.isEmpty(entity)) {
                        LanguageTeacherMappingEntity languageTeacherMappingEntity = new LanguageTeacherMappingEntity();
                        languageTeacherMappingEntity.setTeacherId(teacherId);
                        languageTeacherMappingEntity.setLanguageId(languageId);
                        languageTeacherMappingEntity.setDataset(request.getDataset());
                        languageTeacherMappingRepository.save(languageTeacherMappingEntity);
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
