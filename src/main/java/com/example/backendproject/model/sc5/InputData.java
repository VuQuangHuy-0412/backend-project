package com.example.backendproject.model.sc5;

import com.example.backendproject.entity.sc5.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InputData {
    private List<TeacherEntity> teachers;
    private List<LanguageTeacherMappingEntity> languageTeacherMappings;
    private List<GroupTeacherMappingEntity> groupTeacherMappings;
    private List<SubjectEntity> subjects;
    private List<ClassEntity> classes;
    private List<GroupTeacherEntity> groupTeachers;
    private List<StudentProjectEntity> studentProjects;
    private List<LanguageEntity> languages;

    private Integer numOfTeachers;
    private Integer numOfLanguages;
    private Integer numOfGroups;
    private Integer numOfClasses;
    private Integer numOfSubjects;
    private Integer numOfStudents;
}
