package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.mapper.StudentProjectMapper;
import com.example.backendproject.model.sc5.UploadStudentProjectRequest;
import com.example.backendproject.repository.sc5.StudentProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StudentProjectServiceHelper {
    private final StudentProjectRepository studentProjectRepository;
    private final StudentProjectMapper studentProjectMapper;

    public StudentProjectServiceHelper(StudentProjectRepository studentProjectRepository,
                                       StudentProjectMapper studentProjectMapper) {
        this.studentProjectRepository = studentProjectRepository;
        this.studentProjectMapper = studentProjectMapper;
    }

    @Async("async-thread-pool")
    public void uploadFileStudentProject(UploadStudentProjectRequest request) {
        List<StudentProjectEntity> entities = studentProjectMapper.toEntities(request.getStudentProjectCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        studentProjectRepository.saveAll(entities);
    }
}
