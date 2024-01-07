package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.entity.sc5.StudentProjectEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.StudentProjectMapper;
import com.example.backendproject.model.sc5.StudentProjectUpload;
import com.example.backendproject.model.sc5.UploadStudentProjectRequest;
import com.example.backendproject.repository.sc5.ClassRepository;
import com.example.backendproject.repository.sc5.StudentProjectRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StudentProjectServiceHelper {
    private final StudentProjectRepository studentProjectRepository;
    private final StudentProjectMapper studentProjectMapper;
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;

    public StudentProjectServiceHelper(StudentProjectRepository studentProjectRepository,
                                       StudentProjectMapper studentProjectMapper,
                                       TeacherRepository teacherRepository,
                                       ClassRepository classRepository) {
        this.studentProjectRepository = studentProjectRepository;
        this.studentProjectMapper = studentProjectMapper;
        this.teacherRepository = teacherRepository;
        this.classRepository = classRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileStudentProject(UploadStudentProjectRequest request) {
        for (StudentProjectUpload studentProject : request.getStudentProjectCreateRequests()) {
            StudentProjectEntity entity = new StudentProjectEntity();
            entity.setName(studentProject.getName());
            entity.setStudentCode(studentProject.getStudentCode());
            entity.setProjectType(studentProject.getProjectType());
            entity.setProjectName(studentProject.getProjectName());
            entity.setClassId(studentProject.getClassId());

            Double timeHd = getTimeHd(studentProject, request.getDataset());
            entity.setTimeHd(timeHd);

            List<TeacherEntity> teacher1Entity = teacherRepository.findByFullNameAndDataset(studentProject.getTeacher1Name(), request.getDataset());
            entity.setTeacher1Id(CollectionUtils.isEmpty(teacher1Entity) ? 1L : teacher1Entity.get(0).getId());
            List<TeacherEntity> teacher2Entity = teacherRepository.findByFullNameAndDataset(studentProject.getTeacher2Name(), request.getDataset());
            entity.setTeacher2Id(CollectionUtils.isEmpty(teacher2Entity) ? 1L : teacher2Entity.get(0).getId());
            List<TeacherEntity> teacher3Entity = teacherRepository.findByFullNameAndDataset(studentProject.getTeacher3Name(), request.getDataset());
            entity.setTeacher3Id(CollectionUtils.isEmpty(teacher3Entity) ? 1L : teacher3Entity.get(0).getId());
            entity.setDataset(request.getDataset());
            entity.setCreatedAt(new Date());
            entity.setUpdatedAt(new Date());
            studentProjectRepository.save(entity);
        }
    }

    private Double getTimeHd(StudentProjectUpload studentProjectUpload, Long dataset) {
        String classCode = studentProjectUpload.getClassId();
        String projectType = studentProjectUpload.getProjectType();
        String projectName = studentProjectUpload.getProjectName();
        List<ClassEntity> classEntity = classRepository.findByCodeAndDataset(classCode, dataset);
        if (CollectionUtils.isEmpty(classEntity)) {
            return 0d;
        }
        if ("ĐAMH".equals(projectType)) {
            int numOfCredits = classEntity.get(0).getNumberOfCredits();
            String program = classEntity.get(0).getProgram();
            int semester = Integer.parseInt(classEntity.get(0).getSemester());
            if ("CT CHUẨN".equals(program) && semester % 10 != 3) {
                return (double) numOfCredits * 0.12;
            }
            if ("CT CHUẨN".equals(program)) {
                return (double) numOfCredits * 0.3;
            }
            if ("ELITECH".equals(program)) {
                return (double) numOfCredits * 0.18;
            }
            if ("SIE".equals(program)) {
                return (double) numOfCredits * 0.2;
            }
        } else {
            List<String> DANCCN = List.of("Đồ án nghiên cứu Cử nhân (Khoa học máy tính)", "Đồ án nghiên cứu Cử nhân (CTTN CNTT)");
            List<String> DACN = List.of("Đồ án tốt nghiệp cử nhân", "Đồ án tốt nghiệp");
            int semester = Integer.parseInt(classEntity.get(0).getSemester());
            if (DACN.contains(projectName) && semester % 10 != 3) {
                return 0.8;
            }
            if (DACN.contains(projectName)) {
                return 2.0;
            }
            if (DANCCN.contains(projectName)) {
                return 0.9;
            }
            return 1.0;
        }
        return 0d;
    }
}
