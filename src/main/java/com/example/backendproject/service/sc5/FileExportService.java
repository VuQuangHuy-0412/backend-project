package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.mapper.*;
import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.model.sc5.StudentProject;
import com.example.backendproject.repository.sc5.*;
import com.example.backendproject.service.AdminLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FileExportService {
    private final AdminLogService adminLogService;
    private final ClassRepository classRepository;
    private final ClassMapper classMapper;
    private final StudentProjectRepository studentProjectRepository;
    private final StudentProjectMapper studentProjectMapper;
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    public FileExportService(AdminLogService adminLogService,
                             ClassRepository classRepository, ClassMapper classMapper,
                             StudentProjectRepository studentProjectRepository,
                             StudentProjectMapper studentProjectMapper,
                             TeacherRepository teacherRepository,
                             TeacherMapper teacherMapper,
                             SubjectRepository subjectRepository, SubjectMapper subjectMapper,
                             LanguageRepository languageRepository, LanguageMapper languageMapper) {
        this.adminLogService = adminLogService;
        this.classRepository = classRepository;
        this.classMapper = classMapper;
        this.studentProjectRepository = studentProjectRepository;
        this.studentProjectMapper = studentProjectMapper;
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    public InputStreamResource exportListTimetablingTeacher(Long dataset) {
        adminLogService.log("exportListTimetablingTeacher", String.valueOf(dataset));
        if (dataset == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        List<ClassEntity> entities = classRepository.findByDataset(dataset);

        List<Class> data = classMapper.toDtos(entities);
        for (Class classDto : data) {
            if (classDto.getSubjectId() != null) {
                Optional<SubjectEntity> subjectEntity = subjectRepository.findById(classDto.getSubjectId());
                subjectEntity.ifPresent(entity -> classDto.setSubject(subjectMapper.toDto(entity)));
                subjectEntity.ifPresent(entity -> classDto.setName(entity.getName()));
            }
            if (classDto.getTeacherId() != null) {
                Optional<TeacherEntity> teacherEntity = teacherRepository.findById(classDto.getTeacherId());
                teacherEntity.ifPresent(entity -> classDto.setTeacher(teacherMapper.toDto(entity)));
            }
            if (classDto.getLanguageId() != null) {
                Optional<LanguageEntity> languageEntity = languageRepository.findById(classDto.getLanguageId());
                languageEntity.ifPresent(entity -> classDto.setLanguage(languageMapper.toDto(entity)));
            }
        }

        return createFileExcelListTimeTablingTeacher(data);
    }

    private InputStreamResource createFileExcelListTimeTablingTeacher(List<Class> entities) {
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

            Sheet listTimetablingTeacher = workbook.createSheet("Danh sách lớp học sau phân công");
            // title row
            int row = 0;
            Row rowTitle = listTimetablingTeacher.createRow(row);
            Cell cellTitle = rowTitle.createCell(0);
            cellTitle.setCellValue("DANH SÁCH LỚP HỌC SAU PHÂN CÔNG");
            cellTitle.setCellStyle(styleHeader);
            listTimetablingTeacher.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
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
            listTimetablingTeacher.setColumnWidth(1, ((int) (24 * 1.14388)) * 256);

            Cell cellCode = headerRow.createCell(2);
            cellCode.setCellValue("Mã lớp học");
            cellCode.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(2, ((int) (12 * 1.14388)) * 256);

            Cell cellSemester = headerRow.createCell(3);
            cellSemester.setCellValue("Học kỳ");
            cellSemester.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(3, ((int) (12 * 1.14388)) * 256);

            Cell cellSubjectId = headerRow.createCell(4);
            cellSubjectId.setCellValue("Mã học phần");
            cellSubjectId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(4, ((int) (12 * 1.14388)) * 256);

            Cell cellWeek = headerRow.createCell(5);
            cellWeek.setCellValue("Tuần học");
            cellWeek.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(5, ((int) (12 * 1.14388)) * 256);

            Cell cellDayOfWeek = headerRow.createCell(6);
            cellDayOfWeek.setCellValue("Thứ trong tuần");
            cellDayOfWeek.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(6, ((int) (12 * 1.14388)) * 256);

            Cell cellStartTime = headerRow.createCell(7);
            cellStartTime.setCellValue("Thời gian trong ngày");
            cellStartTime.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(7, ((int) (12 * 1.14388)) * 256);

            Cell cellTimeOfClass = headerRow.createCell(8);
            cellTimeOfClass.setCellValue("Số giờ GD");
            cellTimeOfClass.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(8, ((int) (6 * 1.14388)) * 256);

            Cell cellLanguageId = headerRow.createCell(9);
            cellLanguageId.setCellValue("Ngôn ngữ");
            cellLanguageId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(9, ((int) (12 * 1.14388)) * 256);

            Cell cellRoom = headerRow.createCell(10);
            cellRoom.setCellValue("Phòng học");
            cellRoom.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(10, ((int) (12 * 1.14388)) * 256);

            Cell cellNumberOfStudents = headerRow.createCell(11);
            cellNumberOfStudents.setCellValue("Số lượng SV");
            cellNumberOfStudents.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(11, ((int) (12 * 1.14388)) * 256);

            Cell cellClassType = headerRow.createCell(12);
            cellClassType.setCellValue("Loại lớp");
            cellClassType.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(12, ((int) (6 * 1.14388)) * 256);

            Cell cellProgram = headerRow.createCell(13);
            cellProgram.setCellValue("Chương trình");
            cellProgram.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(13, ((int) (12 * 1.14388)) * 256);

            Cell cellTeacherId = headerRow.createCell(14);
            cellTeacherId.setCellValue("Giảng viên phụ trách");
            cellTeacherId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(14, ((int) (12 * 1.14388)) * 256);

            for (Class classEntity : entities) {
                row += 1;
                Row rowClass = listTimetablingTeacher.createRow(row);

                Cell stt = rowClass.createCell(0);
                stt.setCellValue(row - 1);
                stt.setCellStyle(styleLeft);

                Cell name = rowClass.createCell(1);
                name.setCellValue(StringUtils.isBlank(classEntity.getName()) ? "" : classEntity.getName());
                name.setCellStyle(styleLeft);

                Cell code = rowClass.createCell(2);
                code.setCellValue(StringUtils.isBlank(classEntity.getCode()) ? "" : classEntity.getCode());
                code.setCellStyle(styleLeft);

                Cell semester = rowClass.createCell(3);
                semester.setCellValue(StringUtils.isBlank(classEntity.getSemester()) ? "" : classEntity.getSemester());
                semester.setCellStyle(styleLeft);

                Cell subjectId = rowClass.createCell(4);
                subjectId.setCellValue(classEntity.getSubject() == null ? "" :
                        (StringUtils.isBlank(classEntity.getSubject().getCode()) ? "" : classEntity.getSubject().getCode()));
                subjectId.setCellStyle(styleLeft);

                Cell week = rowClass.createCell(5);
                week.setCellValue(StringUtils.isBlank(classEntity.getWeek()) ? "" : classEntity.getWeek());
                week.setCellStyle(styleLeft);

                Cell dayOfWeek = rowClass.createCell(6);
                dayOfWeek.setCellValue(StringUtils.isBlank(classEntity.getDayOfWeek()) ? "" : classEntity.getDayOfWeek());
                dayOfWeek.setCellStyle(styleLeft);

                Cell startTime = rowClass.createCell(7);
                startTime.setCellValue((classEntity.getStartTime() == null || classEntity.getEndTime() == null) ? "" :
                        (classEntity.getStartTime() + " - " + classEntity.getEndTime()));
                startTime.setCellStyle(styleLeft);

                Cell timeOfClass = rowClass.createCell(8);
                timeOfClass.setCellValue(classEntity.getTimeOfClass() == null ? 0d : classEntity.getTimeOfClass());
                timeOfClass.setCellStyle(styleLeft);

                Cell languageId = rowClass.createCell(9);
                languageId.setCellValue(classEntity.getLanguage() == null ? "" :
                        (StringUtils.isBlank(classEntity.getLanguage().getName()) ? "" : classEntity.getLanguage().getName()));
                languageId.setCellStyle(styleLeft);

                Cell room = rowClass.createCell(10);
                room.setCellValue((StringUtils.isBlank(classEntity.getRoom()) || StringUtils.isBlank(classEntity.getBuilding())) ? "" :
                        classEntity.getBuilding() + "-" + classEntity.getRoom());
                room.setCellStyle(styleLeft);

                Cell numberOfStudent = rowClass.createCell(11);
                numberOfStudent.setCellValue(classEntity.getNumberOfStudent() == null ? "" : String.valueOf(classEntity.getNumberOfStudent()));
                numberOfStudent.setCellStyle(styleLeft);

                Cell classType = rowClass.createCell(12);
                classType.setCellValue(StringUtils.isBlank(classEntity.getClassType()) ? "" : classEntity.getClassType());
                classType.setCellStyle(styleLeft);

                Cell program = rowClass.createCell(13);
                program.setCellValue(StringUtils.isBlank(classEntity.getProgram()) ? "" : classEntity.getProgram());
                program.setCellStyle(styleLeft);

                Cell teacherId = rowClass.createCell(14);
                teacherId.setCellValue(classEntity.getTeacher() == null ? "" :
                        (StringUtils.isBlank(classEntity.getTeacher().getFullName()) ? "" : classEntity.getTeacher().getFullName()));
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

    public InputStreamResource exportListTimetablingStudent(Long dataset) {
        adminLogService.log("exportListTimetablingTeacher", String.valueOf(dataset));
        if (dataset == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        List<StudentProjectEntity> entities = studentProjectRepository.findByDataset(dataset);

        List<StudentProject> data = studentProjectMapper.toDtos(entities);

        for (StudentProject studentProject : data) {
            if (studentProject.getTeacherAssignedId() != null) {
                Optional<TeacherEntity> teacherAssignedEntity = teacherRepository.findById(studentProject.getTeacherAssignedId());
                teacherAssignedEntity.ifPresent(entity -> studentProject.setTeacherAssigned(teacherMapper.toDto(entity)));
            }
        }

        return createFileExcelListTimeTablingStudent(data);
    }

    private InputStreamResource createFileExcelListTimeTablingStudent(List<StudentProject> entities) {
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

            Sheet listTimetablingTeacher = workbook.createSheet("Danh sách sinh viên");
            // title row
            int row = 0;
            Row rowTitle = listTimetablingTeacher.createRow(row);
            Cell cellTitle = rowTitle.createCell(0);
            cellTitle.setCellValue("DANH SÁCH SINH VIÊN SAU PHÂN CÔNG");
            cellTitle.setCellStyle(styleHeader);
            listTimetablingTeacher.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
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
            listTimetablingTeacher.setColumnWidth(1, ((int) (24 * 1.14388)) * 256);

            Cell cellStudentCode = headerRow.createCell(2);
            cellStudentCode.setCellValue("Mã sinh viên");
            cellStudentCode.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(2, ((int) (12 * 1.14388)) * 256);

            Cell cellTimeHd = headerRow.createCell(3);
            cellTimeHd.setCellValue("Số giờ HD");
            cellTimeHd.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(3, ((int) (6 * 1.14388)) * 256);

            Cell cellClassId = headerRow.createCell(4);
            cellClassId.setCellValue("Mã lớp đồ án");
            cellClassId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(4, ((int) (12 * 1.14388)) * 256);

            Cell cellProjectName = headerRow.createCell(5);
            cellProjectName.setCellValue("Tên đồ án");
            cellProjectName.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(5, ((int) (24 * 1.14388)) * 256);

            Cell cellProjectType = headerRow.createCell(6);
            cellProjectType.setCellValue("Loại đồ án");
            cellProjectType.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(6, ((int) (12 * 1.14388)) * 256);

            Cell cellTeacherAssignedId = headerRow.createCell(7);
            cellTeacherAssignedId.setCellValue("Giảng viên hướng dẫn");
            cellTeacherAssignedId.setCellStyle(styleHeader);
            listTimetablingTeacher.setColumnWidth(7, ((int) (12 * 1.14388)) * 256);

            for (StudentProject studentProjectEntity : entities) {
                row += 1;
                Row rowClass = listTimetablingTeacher.createRow(row);

                Cell stt = rowClass.createCell(0);
                stt.setCellValue(row - 1);
                stt.setCellStyle(styleLeft);

                Cell name = rowClass.createCell(1);
                name.setCellValue(StringUtils.isBlank(studentProjectEntity.getName()) ? "" : studentProjectEntity.getName());
                name.setCellStyle(styleLeft);

                Cell studentCode = rowClass.createCell(2);
                studentCode.setCellValue(StringUtils.isBlank(studentProjectEntity.getStudentCode()) ? "" : studentProjectEntity.getStudentCode());
                studentCode.setCellStyle(styleLeft);

                Cell timeHd = rowClass.createCell(3);
                timeHd.setCellValue(studentProjectEntity.getTimeHd() == null ? 0d : studentProjectEntity.getTimeHd());
                timeHd.setCellStyle(styleLeft);

                Cell classId = rowClass.createCell(4);
                classId.setCellValue(StringUtils.isBlank(studentProjectEntity.getClassId()) ? "" : studentProjectEntity.getClassId());
                classId.setCellStyle(styleLeft);

                Cell projectName = rowClass.createCell(5);
                projectName.setCellValue(StringUtils.isBlank(studentProjectEntity.getProjectName()) ? "" : studentProjectEntity.getProjectName());
                projectName.setCellStyle(styleLeft);

                Cell projectType = rowClass.createCell(6);
                projectType.setCellValue(StringUtils.isBlank(studentProjectEntity.getProjectType()) ? "" : studentProjectEntity.getProjectType());
                projectType.setCellStyle(styleLeft);

                Cell teacherAssignedId = rowClass.createCell(7);
                teacherAssignedId.setCellValue(studentProjectEntity.getTeacherAssigned() == null ? "" :
                        (StringUtils.isBlank(studentProjectEntity.getTeacherAssigned().getFullName()) ? "" : studentProjectEntity.getTeacherAssigned().getFullName()));
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
