package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.StudentProject;
import com.example.backendproject.model.sc5.StudentProjectSearchRequest;
import com.example.backendproject.model.sc5.StudentProjectSearchResponse;
import com.example.backendproject.service.sc5.StudentProjectService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentProjectController {
    private final StudentProjectService studentProjectService;

    public StudentProjectController(StudentProjectService studentProjectService) {
        this.studentProjectService = studentProjectService;
    }

    @GetMapping(value = "/student-project/search")
    @ApiDescription(value = "Danh sách giảng viên", code = "student_project_search")
    public StudentProjectSearchResponse searchStudentProject(StudentProjectSearchRequest request) {
        return studentProjectService.searchStudentProject(request);
    }

    @PostMapping(value = "/student-project/create")
    @ApiDescription(value = "Thêm mới giảng viên", code = "student_project_create")
    public void createStudentProject(@RequestBody StudentProject studentProject) {
        studentProjectService.createStudentProject(studentProject);
    }

    @PostMapping(value = "/student-project/update")
    @ApiDescription(value = "Cập nhật thông tin giảng viên", code = "student_project_update")
    public void updateStudentProject(@RequestBody StudentProject studentProject) {
        studentProjectService.updateStudentProject(studentProject);
    }
}
