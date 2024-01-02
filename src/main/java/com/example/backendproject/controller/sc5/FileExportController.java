package com.example.backendproject.controller.sc5;

import com.example.backendproject.service.sc5.FileExportService;
import com.example.backendproject.util.ApiDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FileExportController {
    private final FileExportService fileExportService;

    public FileExportController(FileExportService fileExportService) {
        this.fileExportService = fileExportService;
    }

    @PostMapping(value = "/admin/timetabling-teacher/list/excel")
    @ApiDescription(value = "Xuất file Excel danh sách lớp sau khi phân công", code = "timetabling_teacher_export_excel")
    public ResponseEntity<?> exportListTimetablingTeacher() {
        String fileName = "timetabling_teacher_" + System.currentTimeMillis() + ".xlsx";
        InputStreamResource resource = fileExportService.exportListTimetablingTeacher();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .body(resource);
    }

    @PostMapping(value = "/admin/timetabling-student/list/excel")
    @ApiDescription(value = "Xuất file Excel danh sách sinh viên sau khi phân công", code = "timetabling_student_export_excel")
    public ResponseEntity<?> exportListTimetablingStudent() {
        String fileName = "timetabling_student_" + System.currentTimeMillis() + ".xlsx";
        InputStreamResource resource = fileExportService.exportListTimetablingStudent();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .body(resource);
    }
}
