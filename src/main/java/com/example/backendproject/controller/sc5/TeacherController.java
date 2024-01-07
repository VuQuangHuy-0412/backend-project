package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.*;
import com.example.backendproject.service.sc5.TeacherService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping(value = "/teacher/create")
    @ApiDescription(value = "Thêm mới giảng viên", code = "teacher_create")
    public void createTeacher(@RequestBody Teacher teacher) {
        teacherService.createTeacher(teacher);
    }

    @PostMapping(value = "/teacher/update")
    @ApiDescription(value = "Cập nhật thông tin giảng viên", code = "teacher_update")
    public void updateTeacher(@RequestBody Teacher teacher) {
        teacherService.updateTeacher(teacher);
    }

    @PostMapping(value = "/teacher/upload-excel")
    @ApiDescription(value = "Upload danh sách GV sử dụng excel", code = "teacher_upload_excel")
    public void uploadFileTeacher(@RequestBody UploadTeacherRequest request) {
        teacherService.uploadFileTeacher(request);
    }

    @GetMapping(value = "/teacher/all-by-group")
    @ApiDescription(value = "Lấy toàn bộ danh sách GV thuộc nhóm chuyên môn", code = "all_teacher_by_group")
    public TeacherSearchResponse getAllTeacherByGroup(Long groupId) {
        return teacherService.getAllTeacherByGroup(groupId);
    }

    @GetMapping(value = "/teacher/all")
    @ApiDescription(value = "Lấy toàn bộ danh sách GV", code = "all_teacher")
    public TeacherSearchResponse getAllTeacher() {
        return teacherService.getAllTeacher();
    }

    @PostMapping(value = "/language-teacher/upload-excel")
    @ApiDescription(value = "Upload danh sách GV sử dụng ngôn ngữ sử dụng excel", code = "language_teacher_upload_excel")
    public void uploadFileLanguageTeacherMapping(@RequestBody UploadLanguageTeacherRequest request) {
        teacherService.uploadFileLanguageTeacherMapping(request);
    }

    @PostMapping(value = "/teacher/calculate-time")
    @ApiDescription(value = "Tính toán thời gian giảng dạy và hướng dẫn dựa trên danh sách lớp học và danh sách sinh viên đã upload", code = "teacher_calculate_time")
    public void calculateTimeTeacher() {
        teacherService.calculateTimeTeacher();
    }
}
