package com.example.backendproject.controller.sc5;

import com.example.backendproject.model.sc5.CustomConstraint;
import com.example.backendproject.model.sc5.ConstraintSearchRequest;
import com.example.backendproject.model.sc5.ConstraintSearchResponse;
import com.example.backendproject.model.sc5.RequiredConstraint;
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
    @ApiDescription(value = "Danh sách ràng buộc", code = "constraint_search")
    public ConstraintSearchResponse searchConstraint(ConstraintSearchRequest request) {
        return constraintService.searchConstraint(request);
    }

    @PostMapping(value = "/constraint/custom/create")
    @ApiDescription(value = "Thêm mới ràng buộc", code = "custom_constraint_create")
    public void createCustomConstraint(@RequestBody CustomConstraint customConstraint) {
        constraintService.createCustomConstraint(customConstraint);
    }

    @PostMapping(value = "/constraint/custom/update")
    @ApiDescription(value = "Cập nhật thông tin ràng buộc", code = "custom_constraint_update")
    public void updateCustomConstraint(@RequestBody CustomConstraint customConstraint) {
        constraintService.updateCustomConstraint(customConstraint);
    }

    @PostMapping(value = "/constraint/required/create")
    @ApiDescription(value = "Thêm mới ràng buộc", code = "required_constraint_create")
    public void createRequiredConstraint(@RequestBody RequiredConstraint requiredConstraint) {
        constraintService.createRequiredConstraint(requiredConstraint);
    }

    @PostMapping(value = "/constraint/required/update")
    @ApiDescription(value = "Cập nhật thông tin ràng buộc", code = "required_constraint_update")
    public void updateRequiredConstraint(@RequestBody RequiredConstraint requiredConstraint) {
        constraintService.updateRequiredConstraint(requiredConstraint);
    }
}
