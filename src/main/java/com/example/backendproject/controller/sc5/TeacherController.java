package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.TeacherSearchRequest;
import com.example.backendproject.model.sc5.TeacherSearchResponse;
import com.example.backendproject.service.sc5.TeacherService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping(value = "/teacher/search")
    @ApiDescription(value = "Danh sách giảng viên", code = "teacher_search")
    public TeacherSearchResponse searchTeacher(TeacherSearchRequest request) {
        return teacherService.searchTeacher(request);
    }
}
