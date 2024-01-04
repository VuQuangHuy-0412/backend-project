package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.repository.sc5.ClassRepository;
import com.example.backendproject.repository.sc5.StudentProjectRepository;
import com.example.backendproject.service.AdminLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class FileExportService {
    private final AdminLogService adminLogService;
    private final ClassRepository classRepository;
    private final StudentProjectRepository studentProjectRepository;

    public FileExportService(AdminLogService adminLogService,
                             ClassRepository classRepository,
                             StudentProjectRepository studentProjectRepository) {
        this.adminLogService = adminLogService;
        this.classRepository = classRepository;
        this.studentProjectRepository = studentProjectRepository;
    }

    public InputStreamResource exportListTimetablingTeacher() {
        adminLogService.log("exportListTimetablingTeacher", null);

        List<ClassEntity> entities = classRepository.findAll();

        return createFileExcelListTimeTablingTeacher(entities);
    }

    private InputStreamResource createFileExcelListTimeTablingTeacher(List<ClassEntity> entities) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            // set style of cell: bold, center
            CellStyle styleHeader = workbook.createCellStyle();
            Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            styleHeader.setFont(fontHeader);
            styleHeader.setAlignment(HorizontalAlignment.CENTER);

            // set style of cell: left-center
            CellStyle styleLeft = workbook.createCellStyle();
            styleLeft.setWrapText(true);
            styleLeft.setAlignment(HorizontalAlignment.LEFT);
            styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);

            // set style of cell: right-center
            CellStyle styleRight = workbook.createCellStyle();
            styleRight.setWrapText(true);
            styleRight.setAlignment(HorizontalAlignment.RIGHT);
            styleRight.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle styleLongContent = workbook.createCellStyle();
            styleLongContent.setWrapText(false);
            styleLongContent.setAlignment(HorizontalAlignment.LEFT);
            styleLongContent.setVerticalAlignment(VerticalAlignment.CENTER);

            Sheet listTimetablingTeacher = workbook.createSheet("Danh sách lớp sau phân công");
            // title row
            int row = 0;
            Row rowTitle = listTimetablingTeacher.createRow(row);
            Cell cellTitle = rowTitle.createCell(0);
            cellTitle.setCellValue("DANH SÁCH LỚP HỌC SAU PHÂN CÔNG");
            cellTitle.setCellStyle(styleHeader);
            listTimetablingTeacher.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
            // header row
            row += 1;
            Row headerRow = listTimetablingTeacher.createRow(row);

            Cell cellSTT = headerRow.createCell(0);
            cellSTT.setCellValue("STT");
            cellSTT.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(0, ((int) (6 * 1.14388)) * 256);

            Cell cellName = headerRow.createCell(1);
            cellName.setCellValue("Tên lớp học");
            cellName.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(1, ((int) (6 * 1.14388)) * 256);

            Cell cellCode = headerRow.createCell(2);
            cellCode.setCellValue("Mã lớp học");
            cellCode.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(2, ((int) (24 * 1.14388)) * 256);

            Cell cellSemester = headerRow.createCell(3);
            cellSemester.setCellValue("Học kỳ");
            cellSemester.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(3, ((int) (24 * 1.14388)) * 256);

            Cell cellSubjectId = headerRow.createCell(4);
            cellSubjectId.setCellValue("ID học phần");
            cellSubjectId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(4, ((int) (24 * 1.14388)) * 256);

            Cell cellWeek = headerRow.createCell(5);
            cellWeek.setCellValue("Tuần học");
            cellWeek.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(5, ((int) (24 * 1.14388)) * 256);

            Cell cellDayOfWeek = headerRow.createCell(6);
            cellDayOfWeek.setCellValue("Thứ trong tuần");
            cellDayOfWeek.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(6, ((int) (24 * 1.14388)) * 256);

            Cell cellTimeOfDay = headerRow.createCell(7);
            cellTimeOfDay.setCellValue("Tiết học");
            cellTimeOfDay.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(7, ((int) (24 * 1.14388)) * 256);

            Cell cellTimeOfClass = headerRow.createCell(8);
            cellTimeOfClass.setCellValue("Số giờ GD");
            cellTimeOfClass.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(8, ((int) (24 * 1.14388)) * 256);

            Cell cellLanguageId = headerRow.createCell(9);
            cellLanguageId.setCellValue("ID ngôn ngữ");
            cellLanguageId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(9, ((int) (24 * 1.14388)) * 256);

            Cell cellBuilding = headerRow.createCell(10);
            cellBuilding.setCellValue("Toà nhà");
            cellBuilding.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(10, ((int) (24 * 1.14388)) * 256);

            Cell cellRoom = headerRow.createCell(11);
            cellRoom.setCellValue("Phòng học");
            cellRoom.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(11, ((int) (24 * 1.14388)) * 256);

            Cell cellTeacherId = headerRow.createCell(12);
            cellTeacherId.setCellValue("Giảng viên phụ trách");
            cellTeacherId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(12, ((int) (24 * 1.14388)) * 256);

            for (ClassEntity classEntity : entities) {
                row += 1;
                Row rowClass = listTimetablingTeacher.createRow(row);

                Cell stt = rowClass.createCell(0);
                stt.setCellValue(row - 1);
                stt.setCellStyle(styleLeft);

                Cell name = rowClass.createCell(1);
                name.setCellValue(classEntity.getName());
                name.setCellStyle(styleLeft);

                Cell code = rowClass.createCell(2);
                code.setCellValue(classEntity.getCode());
                code.setCellStyle(styleLeft);

                Cell semester = rowClass.createCell(3);
                semester.setCellValue(classEntity.getSemester());
                semester.setCellStyle(styleLeft);

                Cell subjectId = rowClass.createCell(4);
                subjectId.setCellValue(classEntity.getSubjectId());
                subjectId.setCellStyle(styleLeft);

                Cell week = rowClass.createCell(5);
                week.setCellValue(classEntity.getWeek());
                week.setCellStyle(styleLeft);

                Cell dayOfWeek = rowClass.createCell(6);
                dayOfWeek.setCellValue(classEntity.getDayOfWeek());
                dayOfWeek.setCellStyle(styleLeft);

                Cell timeOfDay = rowClass.createCell(7);
                timeOfDay.setCellValue(classEntity.getTimeOfDay());
                timeOfDay.setCellStyle(styleLeft);

                Cell timeOfClass = rowClass.createCell(8);
                timeOfClass.setCellValue(classEntity.getTimeOfClass());
                timeOfClass.setCellStyle(styleLeft);

                Cell languageId = rowClass.createCell(9);
                languageId.setCellValue(classEntity.getLanguageId());
                languageId.setCellStyle(styleLeft);

                Cell building = rowClass.createCell(10);
                building.setCellValue(classEntity.getBuilding());
                building.setCellStyle(styleLeft);

                Cell room = rowClass.createCell(11);
                room.setCellValue(classEntity.getRoom());
                room.setCellStyle(styleLeft);

                Cell teacherId = rowClass.createCell(12);
                teacherId.setCellValue(classEntity.getRoom());
                teacherId.setCellStyle(styleLeft);
            }

            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return new InputStreamResource(inputStream);
        } catch (Exception exception) {
            log.error("Export timetabling teacher failed", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public InputStreamResource exportListTimetablingStudent() {
        adminLogService.log("exportListTimetablingTeacher", null);

        List<StudentProjectEntity> entities = studentProjectRepository.findAll();

        return createFileExcelListTimeTablingStudent(entities);
    }

    private InputStreamResource createFileExcelListTimeTablingStudent(List<StudentProjectEntity> entities) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            // set style of cell: bold, center
            CellStyle styleHeader = workbook.createCellStyle();
            Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            styleHeader.setFont(fontHeader);
            styleHeader.setAlignment(HorizontalAlignment.CENTER);

            // set style of cell: left-center
            CellStyle styleLeft = workbook.createCellStyle();
            styleLeft.setWrapText(true);
            styleLeft.setAlignment(HorizontalAlignment.LEFT);
            styleLeft.setVerticalAlignment(VerticalAlignment.CENTER);

            // set style of cell: right-center
            CellStyle styleRight = workbook.createCellStyle();
            styleRight.setWrapText(true);
            styleRight.setAlignment(HorizontalAlignment.RIGHT);
            styleRight.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle styleLongContent = workbook.createCellStyle();
            styleLongContent.setWrapText(false);
            styleLongContent.setAlignment(HorizontalAlignment.LEFT);
            styleLongContent.setVerticalAlignment(VerticalAlignment.CENTER);

            Sheet listTimetablingTeacher = workbook.createSheet("Danh sách sinh viên sau phân công");
            // title row
            int row = 0;
            Row rowTitle = listTimetablingTeacher.createRow(row);
            Cell cellTitle = rowTitle.createCell(0);
            cellTitle.setCellValue("DANH SÁCH SINH VIÊN SAU PHÂN CÔNG");
            cellTitle.setCellStyle(styleHeader);
            listTimetablingTeacher.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            // header row
            row += 1;
            Row headerRow = listTimetablingTeacher.createRow(row);

            Cell cellSTT = headerRow.createCell(0);
            cellSTT.setCellValue("STT");
            cellSTT.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(0, ((int) (6 * 1.14388)) * 256);

            Cell cellName = headerRow.createCell(1);
            cellName.setCellValue("Tên sinh viên");
            cellName.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(1, ((int) (6 * 1.14388)) * 256);

            Cell cellStudentCode = headerRow.createCell(2);
            cellStudentCode.setCellValue("Mã sinh viên");
            cellStudentCode.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(2, ((int) (24 * 1.14388)) * 256);

            Cell cellTimeHd = headerRow.createCell(3);
            cellTimeHd.setCellValue("Số giờ HD");
            cellTimeHd.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(3, ((int) (24 * 1.14388)) * 256);

            Cell cellTeacherAssignedId = headerRow.createCell(4);
            cellTeacherAssignedId.setCellValue("Giảng viên phụ trách");
            cellTeacherAssignedId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(4, ((int) (24 * 1.14388)) * 256);

            for (StudentProjectEntity studentProjectEntity : entities) {
                row += 1;
                Row rowClass = listTimetablingTeacher.createRow(row);

                Cell stt = rowClass.createCell(0);
                stt.setCellValue(row - 1);
                stt.setCellStyle(styleLeft);

                Cell name = rowClass.createCell(1);
                name.setCellValue(studentProjectEntity.getName());
                name.setCellStyle(styleLeft);

                Cell studentCode = rowClass.createCell(2);
                studentCode.setCellValue(studentProjectEntity.getStudentCode());
                studentCode.setCellStyle(styleLeft);

                Cell timeHd = rowClass.createCell(3);
                timeHd.setCellValue(studentProjectEntity.getTimeHd());
                timeHd.setCellStyle(styleLeft);

                Cell teacherAssignedId = rowClass.createCell(4);
                teacherAssignedId.setCellValue(studentProjectEntity.getTeacherAssignedId());
                teacherAssignedId.setCellStyle(styleLeft);
            }

            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return new InputStreamResource(inputStream);
        } catch (Exception exception) {
            log.error("Export timetabling student failed", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
