package com.example.backendproject.controller.sc5;

import com.example.backendproject.entity.sc5.TimetablingProcessEntity;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.sc5.*;
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
    public void timetablingTeacher(Long dataset) {
        timetablingService.timetablingTeacher(dataset);
    }

    @PostMapping("/timetabling/student")
    @ApiDescription(value = "Phân công hướng dẫn sinh viên cho giảng viên", code = "timetabling_student")
    public void timetablingStudent(Long dataset) {
        timeTablingStudentService.timetablingStudent(dataset);
    }

    @GetMapping("/timetable/teacher")
    @ApiDescription(value = "Thời khóa biểu GD của GV", code = "timetable_teacher_get")
    public TimetableTeacher getTimeTableOfTeacher(Long teacherId, Long dataset) {
        return timetablingService.getTimeTableOfTeacher(teacherId, dataset);
    }

    @GetMapping("/timetable/student")
    @ApiDescription(value = "Lịch HD của GV", code = "timetable_student_get")
    public TimetableStudent getTimeTableOfStudent(Long teacherId, Long dataset) {
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

    @GetMapping("/timetabling/teacher/evaluate")
    @ApiDescription(value = "Đánh giá kết quả sau phân công GD", code = "timetabling_teacher_evaluate")
    public EvaluateResponse evaluateTimetablingTeacher(Long dataset) {
        return timetablingService.evaluateTimetablingTeacher(dataset);
    }

    @GetMapping("/timetabling/student/evaluate")
    @ApiDescription(value = "Đánh giá kết quả sau phân công HD", code = "timetabling_student_evaluate")
    public EvaluateResponse evaluateTimetablingStudent(Long dataset) {
        return timeTablingStudentService.evaluateTimetablingStudent(dataset);
    }

    @GetMapping("/timetabling/teacher/timetable")
    @ApiDescription(value = "Thời khóa biểu của các giảng viên", code = "timetabling_teacher_timetable")
    public TimetableResponse teacherTimetable(Long dataset, Long teacherId) {
        return timetablingService.teacherTimetable(dataset, teacherId);
    }

    @GetMapping("/timetabling/student/timetable")
    @ApiDescription(value = "Danh sách sinh viên của các giảng viên", code = "timetabling_student_timetable")
    public TimetableStudentResponse studentTimetable(Long dataset, Long teacherId) {
        return timeTablingStudentService.studentTimetable(dataset, teacherId);
    }
}
