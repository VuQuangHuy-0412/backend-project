package com.example.backendproject.controller.sc5;

import com.example.backendproject.entity.sc5.TimetablingProcessEntity;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.sc5.TimetableStudent;
import com.example.backendproject.model.sc5.TimetableTeacher;
import com.example.backendproject.service.sc5.TimeTablingStudentService;
import com.example.backendproject.service.sc5.TimetablingService;
import com.example.backendproject.util.ApiDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public void timetablingTeacher(Long dataset) throws JsonProcessingException {
        timetablingService.timetablingTeacher(dataset);
    }

    @PostMapping("/timetabling/student")
    @ApiDescription(value = "Phân công hướng dẫn sinh viên cho giảng viên", code = "timetabling_student")
    public void timetablingStudent(Long dataset) throws JsonProcessingException {
        timeTablingStudentService.timetablingStudent(dataset);
    }

    @GetMapping("/timetable/teacher")
    @ApiDescription(value = "Thời khóa biểu GD của GV", code = "timetable_teacher_get")
    public TimetableTeacher getTimeTableOfTeacher(@RequestParam(name = "teacherId") Long teacherId, @RequestParam(name = "dataset") Long dataset) {
        return timetablingService.getTimeTableOfTeacher(teacherId, dataset);
    }

    @GetMapping("/timetable/student")
    @ApiDescription(value = "Lịch HD của GV", code = "timetable_student_get")
    public TimetableStudent getTimeTableOfStudent(@RequestParam(name = "teacherId") Long teacherId, @RequestParam(name = "dataset") Long dataset) {
        return timeTablingStudentService.getTimeTableOfStudent(teacherId, dataset);
    }

    @GetMapping("/timetabling-teacher/input-data")
    @ApiDescription(value = "Input data phân công GD", code = "input_data_timetabling_teacher")
    public InputData getTimetablingTeacherInputData(Long dataset) {
        return timetablingService.getTimetablingTeacherInputData(dataset);
    }

    @GetMapping("/timetabling-student/input-data")
    @ApiDescription(value = "Input data phân công HD", code = "input_data_timetabling_student")
    public InputData getTimetablingStudentInputData(Long dataset) {
        return timeTablingStudentService.getTimetablingStudentInputData(dataset);
    }

    @GetMapping("/timetabling/teacher/status")
    @ApiDescription(value = "Lấy trạng thái phân công GD hiện tại", code = "timetabling_teacher_status")
    public TimetablingProcessEntity getTimetablingTeacherStatus(Long dataset) {
        return timetablingService.getTimetablingTeacherStatus(dataset);
    }

    @GetMapping("/timetabling/student/status")
    @ApiDescription(value = "Lấy trạng thái phân công HD hiện tại", code = "timetabling_student_status")
    public TimetablingProcessEntity getTimetablingStudentStatus(Long dataset) {
        return timetablingService.getTimetablingStudentStatus(dataset);
    }
}
