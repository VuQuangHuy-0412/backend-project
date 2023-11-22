package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.model.sc5.ClassSearchRequest;
import com.example.backendproject.model.sc5.ClassSearchResponse;
import com.example.backendproject.service.sc5.ClassService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping(value = "/class/search")
    @ApiDescription(value = "Danh sách lớp học", code = "class_search")
    public ClassSearchResponse searchClass(ClassSearchRequest request) {
        return classService.searchClass(request);
    }

    @PostMapping(value = "/class/create")
    @ApiDescription(value = "Thêm mới lớp học", code = "class_create")
    public void createClass(@RequestBody Class classDto) {
        classService.createClass(classDto);
    }

    @PostMapping(value = "/class/update")
    @ApiDescription(value = "Cập nhật thông tin lớp học", code = "class_update")
    public void updateClass(@RequestBody Class classDto) {
        classService.updateClass(classDto);
    }
}
