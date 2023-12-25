package com.example.backendproject.controller.sc5;

import com.example.backendproject.service.sc5.TimetablingService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimetablingController {
    private final TimetablingService timetablingService;

    public TimetablingController(TimetablingService timetablingService) {
        this.timetablingService = timetablingService;
    }

    @PostMapping("/timetabling/teacher")
    @ApiDescription(value = "Phân công giảng dạy lớp học cho giảng viên", code = "timetabling_teacher")
    public void timetablingTeacher() {
        timetablingService.timetablingTeacher();
    }

    @PostMapping("/timetabling/student")
    @ApiDescription(value = "Phân công hướng dẫn sinh viên cho giảng viên", code = "timetabling_student")
    public void timetablingStudent() {
        timetablingService.timetablingStudent();
    }
}
