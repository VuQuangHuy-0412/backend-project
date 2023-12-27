package com.example.backendproject.controller.sc5;

import com.example.backendproject.service.sc5.TimeTablingStudentService;
import com.example.backendproject.service.sc5.TimetablingService;
import com.example.backendproject.util.ApiDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimetablingController {
    private final TimetablingService timetablingService;
    private final TimeTablingStudentService timeTablingStudentService;

    public TimetablingController(TimetablingService timetablingService,
                                 TimeTablingStudentService timeTablingStudentService) {
        this.timetablingService = timetablingService;
        this.timeTablingStudentService = timeTablingStudentService;
    }

    @PostMapping("/timetabling/teacher")
    @ApiDescription(value = "Phân công giảng dạy lớp học cho giảng viên", code = "timetabling_teacher")
    public void timetablingTeacher() throws JsonProcessingException {
        timetablingService.timetablingTeacher();
    }

    @PostMapping("/timetabling/student")
    @ApiDescription(value = "Phân công hướng dẫn sinh viên cho giảng viên", code = "timetabling_student")
    public void timetablingStudent() throws JsonProcessingException {
        timeTablingStudentService.timetablingStudent();
    }
}
