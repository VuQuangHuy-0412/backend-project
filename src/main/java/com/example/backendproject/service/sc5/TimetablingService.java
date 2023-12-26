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
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    @Async("async-thread-pool")
    public void timetablingTeacher() {
//        adminLogService.log("timetablingTeacher", null);

        InputData inputData = getInputData();
        Population population = initPopulation(inputData);

        for (int i = 0; i < NUM_LOOP; i++) {
            evaluateConstraint(inputData, population);
            if (i == NUM_LOOP - 1) {
                break;
            }
            selection(population);
            crossover(inputData, population);
            mutation(inputData, population);
            log.info("End loop {}", i);
        }

        Population.Member bestSolution = getTheMostObjectiveResult(population);
        if (bestSolution != null) {
            bestSolution.setObjective(objectiveFunction(inputData, bestSolution));
        }
    }

    private Population.Member getTheMostObjectiveResult(Population population) {
        List<Population.Member> listMember = population.getPopulation();
        List<Double> objectiveTemp = new ArrayList<>(listMember.stream().map(Population.Member::getObjective).toList());
        Collections.sort(objectiveTemp);

        Double mostObjective = objectiveTemp.get(0);
        for (Population.Member member : listMember) {
            if (member.getObjective().equals(mostObjective)) {
                return member;
            }
        }

        return null;
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
            double diff = Math.abs(inputData.getAverageGD() * teacherEntity.getGdTime() - timeTeaching);
            objective = Math.max(objective, diff);
        }
        return objective;
    }

    private void evaluateConstraint(InputData inputData, Population population) {
        clearPopulationObjective(population);
        List<RequiredConstraintEntity> requiredConstraints = inputData.getRequiredConstraints();
        List<RequiredConstraintEntity> CT1 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT1") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT2 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT2") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT3 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT3") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT4 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT4") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT5 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT5") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT6 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT6") && x.getStatus().equals(1)).toList();

        for (Population.Member member : population.getPopulation()) {
            log.info("Start member {}", member);
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

            // CT6: 2 lớp bị conflict lịch học hoặc phòng học không được xếp cùng 1 GV
            if (!CollectionUtils.isEmpty(CT6)) {
                for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                    for (ClassEntity classEntity1 : inputData.getClasses()) {
                        for (ClassEntity classEntity2 : inputData.getClasses()) {
                            if (classEntity1.getId() < classEntity2.getId() && isTeacherOfClass(member, teacherEntity, classEntity1) == 1 &&
                                    isTeacherOfClass(member, teacherEntity, classEntity2) == 1 &&
                                    isConflictClass(classEntity1, classEntity2) == 1) {
                                member.setObjective(member.getObjective() + 100000);
                            }
                        }
                    }
                }
            }
        }
    }

    private void selection(Population population) {
        List<Population.Member> listMember = population.getPopulation();
        List<Double> objectiveTemp = new ArrayList<>(listMember.stream().map(Population.Member::getObjective).toList());
        Collections.sort(objectiveTemp);

        int limitIndex = listMember.size() * 80 / 100;
        Double objectiveLimit = objectiveTemp.get(limitIndex);

        for (Population.Member member : listMember) {
            if (member.getObjective() > objectiveLimit) {
                Random rand = new Random();
                member.setDetails(listMember.get(rand.nextInt(listMember.size())).getDetails());
            }
        }
    }

    private void crossover(InputData inputData, Population population) {
        List<Population.Member> listMember = population.getPopulation();
        for (int i = 0; i < NUM_OF_CROSS; i++) {
            Random rand = new Random();
            int father = rand.nextInt(listMember.size());
            int mother = rand.nextInt(listMember.size());
            for (int j = 0; j < inputData.getNumOfClasses(); j++) {
                // keep 1/2 father and replace 1/2 father by mother and same mother
                if (j % 2 == 0) {
                    for (TeacherEntity teacher1 : inputData.getTeachers()) {
                        for (TeacherEntity teacher2 : inputData.getTeachers()) {
                            if (isTeacherOfClass(listMember.get(father), teacher1, inputData.getClasses().get(j)) == 1 &&
                                    isTeacherOfClass(listMember.get(mother), teacher2, inputData.getClasses().get(j)) == 1) {
                                setTeacherForClass(listMember.get(father), teacher2, inputData.getClasses().get(j));
                                setTeacherForClass(listMember.get(mother), teacher1, inputData.getClasses().get(j));
                            }
                        }
                    }
                }
            }
        }
    }

    private void mutation(InputData inputData, Population population) {
        Random rand = new Random();
        Population.Member member = population.getPopulation().get(rand.nextInt(population.getPopulation().size()));

        ClassEntity class1 = inputData.getClasses().get(rand.nextInt(inputData.getNumOfClasses()));
        List<ClassEntity> newList = inputData.getClasses().stream().filter(x -> !x.getId().equals(class1.getId())).toList();
        ClassEntity class2 = newList.get(rand.nextInt(inputData.getNumOfClasses() - 1));

        for (TeacherEntity teacher1 : inputData.getTeachers()) {
            for (TeacherEntity teacher2 : inputData.getTeachers()) {
                if (isTeacherOfClass(member, teacher1, class1) == 1 && isTeacherOfClass(member, teacher2, class2) == 1) {
                    setTeacherForClass(member, teacher1, class2);
                    setTeacherForClass(member, teacher2, class1);
                }
            }
        }
    }

    private void setTeacherForClass(Population.Member member, TeacherEntity teacherEntity, ClassEntity classEntity) {
        for (Population.Member.MemberDetail detail : member.getDetails()) {
            if (detail.getClassId().equals(classEntity.getId())) {
                detail.setTeacherId(teacherEntity.getId());
            }
        }
    }

    private int isConflictClass(ClassEntity class1, ClassEntity class2) {
        try {
            if (class1.equals(class2)) {
                return 0;
            }
            String[] timeOfDay1 = class1.getTimeOfDay().split(",");
            String[] timeOfDay2 = class1.getTimeOfDay().split(",");
            if (!StringUtils.isBlank(class1.getDayOfWeek()) && !StringUtils.isBlank(class2.getDayOfWeek()) && class1.getDayOfWeek().equals(class2.getDayOfWeek())) {
                for (String t1 : timeOfDay1) {
                    for (String t2 : timeOfDay2) {
                        if (t1.equals(t2)) {
                            return 1;
                        }
                    }
                }
            }

            if (!StringUtils.isBlank(class1.getBuilding()) && !StringUtils.isBlank(class2.getBuilding()) && !class1.getBuilding().equals(class2.getBuilding())) {
                if (Integer.parseInt(timeOfDay1[timeOfDay1.length - 1]) + 1 == Integer.parseInt(timeOfDay2[0]) ||
                        Integer.parseInt(timeOfDay2[timeOfDay2.length - 1]) + 1 == Integer.parseInt(timeOfDay1[0])) {
                    return 1;
                }
            }
            return 0;
        } catch(Exception ex) {
            log.error("Check class conflict failed", ex);
            return 0;
        }
    }

    private void clearPopulationObjective(Population population) {
        for (Population.Member member : population.getPopulation()) {
            member.setObjective(0D);
        }
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
