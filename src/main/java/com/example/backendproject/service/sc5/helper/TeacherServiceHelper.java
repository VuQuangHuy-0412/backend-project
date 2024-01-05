package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.LanguageTeacherMappingEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.LanguageTeacherMappingMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.LanguageTeacherMapping;
import com.example.backendproject.model.sc5.UploadLanguageTeacherRequest;
import com.example.backendproject.model.sc5.UploadTeacherRequest;
import com.example.backendproject.repository.sc5.LanguageTeacherMappingRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public TeacherServiceHelper(TeacherMapper teacherMapper,
                                TeacherRepository teacherRepository,
                                LanguageTeacherMappingMapper languageTeacherMappingMapper,
                                LanguageTeacherMappingRepository languageTeacherMappingRepository) {
        this.teacherMapper = teacherMapper;
        this.teacherRepository = teacherRepository;
        this.languageTeacherMappingMapper = languageTeacherMappingMapper;
        this.languageTeacherMappingRepository = languageTeacherMappingRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileTeacher(UploadTeacherRequest request) {
        List<TeacherEntity> entities = teacherMapper.toEntities(request.getTeacherCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        teacherRepository.saveAll(entities);
    }

    @Async("async-thread-pool")
    public void uploadFileLanguageTeacherMapping(UploadLanguageTeacherRequest request) {
        List<LanguageTeacherMapping> requests = request.getLanguageTeacherCreateRequests();
        for (LanguageTeacherMapping languageTeacherMapping : request.getLanguageTeacherCreateRequests()) {
            LanguageTeacherMappingEntity entity = languageTeacherMappingRepository.findByTeacherIdAndLanguageId(
                    languageTeacherMapping.getTeacherId(), languageTeacherMapping.getLanguageId());
            if (entity != null) {
                List<LanguageTeacherMapping> filter = requests.stream().filter(x ->
                        Objects.equals(x.getLanguageId(), languageTeacherMapping.getLanguageId()) &&
                                Objects.equals(x.getTeacherId(), languageTeacherMapping.getTeacherId())).toList();
                requests.removeAll(filter);
            }
        }

        List<LanguageTeacherMappingEntity> entities = languageTeacherMappingMapper.toEntities(request.getLanguageTeacherCreateRequests());
        languageTeacherMappingRepository.saveAll(entities);
    }
}
