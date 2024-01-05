package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.mapper.SubjectMapper;
import com.example.backendproject.model.sc5.UploadSubjectRequest;
import com.example.backendproject.repository.sc5.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SubjectServiceHelper {
    private final SubjectMapper subjectMapper;
    private final SubjectRepository subjectRepository;

    public SubjectServiceHelper(SubjectMapper subjectMapper,
                                SubjectRepository subjectRepository) {
        this.subjectMapper = subjectMapper;
        this.subjectRepository = subjectRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileSubject(UploadSubjectRequest request) {
        List<SubjectEntity> entities = subjectMapper.toEntities(request.getSubjectCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        for (SubjectEntity entity : entities) {
            subjectRepository.save(entity);
        }
    }
}
