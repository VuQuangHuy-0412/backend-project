package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.geneticalgorithm.Population;
import com.example.backendproject.repository.sc5.*;
import com.example.backendproject.service.AdminLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private final RequiredConstraintRepository requiredConstraintRepository;
    private final CustomConstraintRepository customConstraintRepository;
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
                              RequiredConstraintRepository requiredConstraintRepository,
                              CustomConstraintRepository customConstraintRepository,
                              AdminLogService adminLogService) {
        this.teacherRepository = teacherRepository;
        this.languageTeacherMappingRepository = languageTeacherMappingRepository;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.subjectRepository = subjectRepository;
        this.classRepository = classRepository;
        this.groupTeacherRepository = groupTeacherRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.languageRepository = languageRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.customConstraintRepository = customConstraintRepository;
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
        List<RequiredConstraintEntity> requiredConstraints = requiredConstraintRepository.findAll();
        List<CustomConstraintEntity> customConstraints = customConstraintRepository.findAll();

        inputData.setTeachers(teachers);
        inputData.setLanguageTeacherMappings(languageTeacherMappings);
        inputData.setGroupTeacherMappings(groupTeacherMappings);
        inputData.setSubjects(subjects);
        inputData.setClasses(classes);
        inputData.setGroupTeachers(groupTeachers);
        inputData.setStudentProjects(studentProjects);
        inputData.setLanguages(languages);
        inputData.setRequiredConstraints(requiredConstraints);
        inputData.setCustomConstraints(customConstraints);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfClasses(classes.size());
        inputData.setNumOfGroups(groupTeachers.size());
        inputData.setNumOfSubjects(subjects.size());
        inputData.setNumOfLanguages(languages.size());
        inputData.setNumOfStudents(studentProjects.size());

        int allTimeGdTeacher = teachers.stream().map(TeacherEntity::getGdTime).reduce(0, Integer::sum);
        int allTimeHdTeacher = teachers.stream().map(TeacherEntity::getHdTime).reduce(0, Integer::sum);
        Integer allTimeClass = classes.stream().map(ClassEntity::getTimeOfClass).reduce(0, Integer::sum);
        Integer allTimeHd = studentProjects.stream().map(StudentProjectEntity::getTimeHd).reduce(0, Integer::sum);

        if (allTimeGdTeacher <= 0 || allTimeHdTeacher <= 0) {
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }

        Double averageGD = (double) allTimeClass / allTimeGdTeacher;
        Double averageHD = (double) allTimeHd / allTimeHdTeacher;
        inputData.setAverageGD(averageGD);
        inputData.setAverageHD(averageHD);

        return inputData;
    }

    private Population initPopulation(InputData inputData) {
        Population population = new Population();
        List<Population.Member> members = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Population.Member member = new Population.Member();
            List<Population.Member.MemberDetail> details = new ArrayList<>();
            for (ClassEntity classEntity : inputData.getClasses()) {
                Population.Member.MemberDetail detail = new Population.Member.MemberDetail();
                detail.setClassId(classEntity.getId());
                detail.setTeacherId(getRandomTeacherFromList(inputData.getTeachers()).getId());
                details.add(detail);
            }
            member.setDetails(details);
            members.add(member);
        }
        population.setPopulation(members);
        return population;
    }

    private double objectiveFunction(InputData inputData, Population.Member member) {
        double objective = 0;
        for (TeacherEntity teacherEntity : inputData.getTeachers()) {
            int timeTeaching = 0;
            for (ClassEntity classEntity : inputData.getClasses()) {
                if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                    timeTeaching += classEntity.getTimeOfClass();
                }
            }
            double diff = inputData.getAverageGD() * teacherEntity.getGdTime() - timeTeaching;
            objective = Math.max(objective, diff);
        }
        return objective;
    }

    private Population evaluateConstraint(InputData inputData, Population population) {
        population = clearPopulationObjective(population);
        List<RequiredConstraintEntity> requiredConstraints = inputData.getRequiredConstraints();
        List<RequiredConstraintEntity> CT1 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT1") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT2 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT2") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT3 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT3") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT4 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT4") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT5 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT5") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT6 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT6") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT7 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT7") && x.getStatus().equals(1)).toList();

        for (Population.Member member : population.getPopulation()) {
            member.setObjective(objectiveFunction(inputData, member));

            // CT1: Language - Teacher - Class
            if (!CollectionUtils.isEmpty(CT1)) {
                for (ClassEntity classEntity : inputData.getClasses()) {
                    int ct1 = 0;
                    for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                        if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                            for (LanguageEntity languageEntity : inputData.getLanguages()) {
                                ct1 += classHasLanguage(languageEntity, classEntity) * teacherHasLanguage(languageEntity, teacherEntity, inputData.getLanguageTeacherMappings());
                            }
                        }
                    }
                    if (ct1 != 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            // CT2: Class - Teacher - Subject - GroupTeacher
            if (!CollectionUtils.isEmpty(CT2)) {
                for (ClassEntity classEntity : inputData.getClasses()) {
                    int ct2 = 0;
                    for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                        if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                            for (GroupTeacherEntity groupTeacherEntity : inputData.getGroupTeachers()) {
                                for (SubjectEntity subjectEntity : inputData.getSubjects()) {
                                    ct2 += teacherOfGroup(groupTeacherEntity, teacherEntity, inputData.getGroupTeacherMappings()) *
                                            groupHasSubject(subjectEntity, groupTeacherEntity) * subjectHasClass(subjectEntity, classEntity);
                                }
                            }
                        }
                    }
                    if (ct2 != 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            // CT3: 1 class is only assigned by 1 teacher
            if (!CollectionUtils.isEmpty(CT3)) {
                for (ClassEntity classEntity : inputData.getClasses()) {
                    int ct3 = 0;
                    for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                        if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                            ct3 += 1;
                        }
                    }
                    if (ct3 != 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            // CT4: Each teacher has at least 1 class
            if (!CollectionUtils.isEmpty(CT4)) {
                for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                    int ct4 = 0;
                    for (ClassEntity classEntity : inputData.getClasses()) {
                        if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                            ct4 += 1;
                        }
                    }
                    if (ct4 < 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            // CT5: Số giờ được phân công cho 1 GV không được vượt quá 120% * gdTime của GV * averageGD
            if (!CollectionUtils.isEmpty(CT5)) {
                for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                    int ct5 = 0;
                    for (ClassEntity classEntity : inputData.getClasses()) {
                        if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                            ct5 += classEntity.getTimeOfClass();
                        }
                    }
                    double maxTime = 1.2 * teacherEntity.getGdTime() * inputData.getAverageGD();
                    if (ct5 > maxTime) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }
        }
        return population;
    }

    private Population clearPopulationObjective(Population population) {
        for (Population.Member member : population.getPopulation()) {
            member.setObjective(null);
        }

        return population;
    }

    private int isTeacherOfClass(Population.Member member, TeacherEntity teacherEntity, ClassEntity classEntity) {
        List<Population.Member.MemberDetail> memberDetail = member.getDetails().stream()
                .filter(x -> (x.getTeacherId().equals(teacherEntity.getId()) && x.getClassId().equals(classEntity.getId()))).toList();

        return (CollectionUtils.isEmpty(memberDetail)) ? 0 : 1;
    }

    private int classHasLanguage(LanguageEntity languageEntity, ClassEntity classEntity) {
        return classEntity.getLanguageId().equals(languageEntity.getId()) ? 1 : 0;
    }

    private int teacherHasLanguage(LanguageEntity languageEntity, TeacherEntity teacherEntity, List<LanguageTeacherMappingEntity> ltMappings) {
        List<LanguageTeacherMappingEntity> ltMapping = ltMappings.stream()
                .filter(x -> x.getLanguageId().equals(languageEntity.getId()) && x.getTeacherId().equals(teacherEntity.getId()))
                .toList();

        return (CollectionUtils.isEmpty(ltMapping)) ? 0 : 1;
    }

    private int teacherOfGroup(GroupTeacherEntity groupTeacherEntity, TeacherEntity teacherEntity, List<GroupTeacherMappingEntity> gtMappings) {
        List<GroupTeacherMappingEntity> gtMapping = gtMappings.stream()
                .filter(x -> x.getGroupId().equals(groupTeacherEntity.getId()) && x.getTeacherId().equals(teacherEntity.getId()))
                .toList();

        return (CollectionUtils.isEmpty(gtMapping)) ? 0 : 1;
    }

    private int groupHasSubject(SubjectEntity subjectEntity, GroupTeacherEntity groupTeacherEntity) {
        return subjectEntity.getGroupId().equals(groupTeacherEntity.getId()) ? 1 : 0;
    }

    private int subjectHasClass(SubjectEntity subjectEntity, ClassEntity classEntity) {
        return classEntity.getSubjectId().equals(subjectEntity.getId()) ? 1 : 0;
    }

    private TeacherEntity getRandomTeacherFromList(List<TeacherEntity> teachers) {
        Random rand = new Random();
        return teachers.get(rand.nextInt(teachers.size()));
    }

    public void timetablingStudent() {

    }
}
