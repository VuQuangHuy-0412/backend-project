package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.Constraint;
import com.example.backendproject.model.sc5.ConstraintSearchRequest;
import com.example.backendproject.model.sc5.ConstraintSearchResponse;
import com.example.backendproject.service.sc5.ConstraintService;
import com.example.backendproject.util.ApiDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConstraintController {
    private final ConstraintService constraintService;

    public ConstraintController(ConstraintService constraintService) {
        this.constraintService = constraintService;
    }

    @GetMapping(value = "/constraint/search")
    @ApiDescription(value = "Danh sách giảng viên", code = "constraint_search")
    public ConstraintSearchResponse searchConstraint(ConstraintSearchRequest request) {
        return constraintService.searchConstraint(request);
    }

    @PostMapping(value = "/constraint/create")
    @ApiDescription(value = "Thêm mới giảng viên", code = "constraint_create")
    public void createConstraint(@RequestBody Constraint constraint) {
        constraintService.createConstraint(constraint);
    }

    @PostMapping(value = "/constraint/update")
    @ApiDescription(value = "Cập nhật thông tin giảng viên", code = "constraint_update")
    public void updateConstraint(@RequestBody Constraint constraint) {
        constraintService.updateConstraint(constraint);
    }
}
