package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.Teacher;
import com.example.backendproject.model.sc5.TeacherSearchRequest;
import com.example.backendproject.model.sc5.TeacherSearchResponse;
import com.example.backendproject.model.sc5.UploadTeacherRequest;
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

    @PostMapping(value = "/teacher/all-by-group")
    @ApiDescription(value = "Lấy toàn bộ danh sách GV thuộc nhóm chuyên môn", code = "all_teacher_by_group")
    public TeacherSearchResponse getAllTeacherByGroup(Long groupId) {
        return teacherService.getAllTeacherByGroup(groupId);
    }
}
