package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.Subject;
import com.example.backendproject.model.sc5.SubjectSearchRequest;
import com.example.backendproject.model.sc5.SubjectSearchResponse;
import com.example.backendproject.service.sc5.SubjectService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping(value = "/subject/search")
    @ApiDescription(value = "Danh sách học phần", code = "subject_search")
    public SubjectSearchResponse searchSubject(SubjectSearchRequest request) {
        return subjectService.searchSubject(request);
    }

    @PostMapping(value = "/subject/create")
    @ApiDescription(value = "Thêm mới học phần", code = "subject_create")
    public void createSubject(@RequestBody Subject subject) {
        subjectService.createSubject(subject);
    }

    @PostMapping(value = "/subject/update")
    @ApiDescription(value = "Cập nhật thông tin học phần", code = "subject_update")
    public void updateSubject(@RequestBody Subject subject) {
        subjectService.updateSubject(subject);
    }
}
