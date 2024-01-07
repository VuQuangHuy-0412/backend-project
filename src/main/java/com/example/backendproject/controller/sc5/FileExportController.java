package com.example.backendproject.controller.sc5;

import com.example.backendproject.service.sc5.FileExportService;
import com.example.backendproject.util.ApiDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FileExportController {
    private final FileExportService fileExportService;

    public FileExportController(FileExportService fileExportService) {
        this.fileExportService = fileExportService;
    }

    @GetMapping(value = "/admin/timetabling-teacher/list/excel")
    @ApiDescription(value = "Xuất file Excel danh sách lớp sau khi phân công", code = "timetabling_teacher_export_excel")
    public ResponseEntity<?> exportListTimetablingTeacher(Long dataset) {
        String fileName = "timetabling_teacher_" + System.currentTimeMillis() + ".xlsx";
        InputStreamResource resource = fileExportService.exportListTimetablingTeacher(dataset);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .body(resource);
    }

    @GetMapping(value = "/admin/timetabling-student/list/excel")
    @ApiDescription(value = "Xuất file Excel danh sách sinh viên sau khi phân công", code = "timetabling_student_export_excel")
    public ResponseEntity<?> exportListTimetablingStudent(Long dataset) {
        String fileName = "timetabling_student_" + System.currentTimeMillis() + ".xlsx";
        InputStreamResource resource = fileExportService.exportListTimetablingStudent(dataset);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .body(resource);
    }
}
