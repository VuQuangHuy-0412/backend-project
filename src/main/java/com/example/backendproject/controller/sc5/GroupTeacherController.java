package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.*;
import com.example.backendproject.service.sc5.GroupTeacherService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupTeacherController {
    private final GroupTeacherService groupTeacherService;

    public GroupTeacherController(GroupTeacherService groupTeacherService) {
        this.groupTeacherService = groupTeacherService;
    }

    @GetMapping(value = "/group-teacher/search")
    @ApiDescription(value = "Danh sách nhóm chuyên môn", code = "group_teacher_search")
    public GroupTeacherSearchResponse searchGroupTeacher(GroupTeacherSearchRequest request) {
        return groupTeacherService.searchGroupTeacher(request);
    }

    @PostMapping(value = "/group-teacher/create")
    @ApiDescription(value = "Thêm mới nhóm chuyên môn", code = "group_teacher_create")
    public void createGroupTeacher(@RequestBody GroupTeacher groupTeacher) {
        groupTeacherService.createGroupTeacher(groupTeacher);
    }

    @PostMapping(value = "/group-teacher/update")
    @ApiDescription(value = "Cập nhật thông tin nhóm chuyên môn", code = "group_teacher_update")
    public void updateGroupTeacher(@RequestBody GroupTeacher groupTeacher) {
        groupTeacherService.updateGroupTeacher(groupTeacher);
    }

    @PostMapping(value = "/group-teacher/upload-excel")
    @ApiDescription(value = "Upload danh sách NCM sử dụng excel", code = "group_teacher_upload_excel")
    public void uploadFileGroupTeacher(@RequestBody UploadGroupTeacherRequest request) {
        groupTeacherService.uploadFileGroupTeacher(request);
    }

    @GetMapping(value = "/group-teacher/all")
    @ApiDescription(value = "Lấy danh sách toàn bộ nhóm chuyên môn", code = "group_teacher_get_all")
    public GroupTeacherSearchResponse getAllGroupTeacher() {
        return groupTeacherService.getAllGroupTeacher();
    }
}
