package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.model.sc5.UploadClassRequest;
import com.example.backendproject.repository.sc5.ClassRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ClassServiceHelper {
    private final ClassMapper classMapper;
    private final ClassRepository classRepository;

    public ClassServiceHelper(ClassMapper classMapper, ClassRepository classRepository) {
        this.classMapper = classMapper;
        this.classRepository = classRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileClass(UploadClassRequest request) {
        List<ClassEntity> entities = classMapper.toEntities(request.getClassCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        for (ClassEntity classEntity : entities) {
            classRepository.save(classEntity);
        }
    }
}
