package com.example.backendproject.service.sc5;

import com.example.backendproject.model.sc5.TeacherSearchRequest;
import com.example.backendproject.model.sc5.TeacherSearchResponse;
import com.example.backendproject.repository.sc5.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public TeacherSearchResponse searchTeacher(TeacherSearchRequest request) {
        TeacherSearchResponse response = new TeacherSearchResponse();
        return response;
    }
}
