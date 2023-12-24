package com.example.backendproject.service;

import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.model.sc5.InputData;
import com.example.backendproject.model.sc5.StudentProject;
import com.example.backendproject.model.sc5.Subject;
import com.example.backendproject.repository.sc5.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TimetablingService {
    private final TeacherRepository teacherRepository;
    private final LanguageTeacherMappingRepository languageTeacherMappingRepository;
    private final GroupTeacherMappingRepository groupTeacherMappingRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;
    private final GroupTeacherRepository groupTeacherRepository;
    private final StudentProjectRepository studentProjectRepository;
    private final LanguageRepository languageRepository;
    private final AdminLogService adminLogService;
    public static final Integer POPULATION_SIZE = 500;
    public static final Integer NUM_OF_CROSS = 50;
    public static final Integer NUM_LOOP = 100;

    public TimetablingService(TeacherRepository teacherRepository,
                              LanguageTeacherMappingRepository languageTeacherMappingRepository,
                              GroupTeacherMappingRepository groupTeacherMappingRepository,
                              SubjectRepository subjectRepository,
                              ClassRepository classRepository,
                              GroupTeacherRepository groupTeacherRepository,
                              StudentProjectRepository studentProjectRepository,
                              LanguageRepository languageRepository,
                              AdminLogService adminLogService) {
        this.teacherRepository = teacherRepository;
        this.languageTeacherMappingRepository = languageTeacherMappingRepository;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.subjectRepository = subjectRepository;
        this.classRepository = classRepository;
        this.groupTeacherRepository = groupTeacherRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.languageRepository = languageRepository;
        this.adminLogService = adminLogService;
    }

    public void timetablingTeacher() {
        adminLogService.log("timetablingTeacher", null);

    }

    private InputData getInputData() {
        InputData inputData = new InputData();
        List<TeacherEntity> teachers = teacherRepository.findAllByStatus(TeacherConstant.Status.ACTIVE);
        List<LanguageTeacherMappingEntity> languageTeacherMappings = languageTeacherMappingRepository.findAll();
        List<GroupTeacherMappingEntity> groupTeacherMappings = groupTeacherMappingRepository.findAll();
        List<SubjectEntity> subjects = subjectRepository.findAll();
        List<ClassEntity> classes = classRepository.findAll();
        List<GroupTeacherEntity> groupTeachers = groupTeacherRepository.findAll();
        List<StudentProjectEntity> studentProjects = studentProjectRepository.findAll();
        List<LanguageEntity> languages = languageRepository.findAll();

        inputData.setTeachers(teachers);
        inputData.setLanguageTeacherMappings(languageTeacherMappings);
        inputData.setGroupTeacherMappings(groupTeacherMappings);
        inputData.setSubjects(subjects);
        inputData.setClasses(classes);
        inputData.setGroupTeachers(groupTeachers);
        inputData.setStudentProjects(studentProjects);
        inputData.setLanguages(languages);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfClasses(classes.size());
        inputData.setNumOfGroups(groupTeachers.size());
        inputData.setNumOfSubjects(subjects.size());
        inputData.setNumOfLanguages(languages.size());
        inputData.setNumOfStudents(studentProjects.size());

        return inputData;
    }

    private void initPopulation(InputData inputData) {

    }

    public void timetablingStudent() {

    }
}
