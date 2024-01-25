package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ClassConstant;
import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.geneticalgorithm.Population;
import com.example.backendproject.model.sc5.TeacherClassMapping;
import com.example.backendproject.repository.sc5.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class TimetablingServiceHelper {
    private final TeacherRepository teacherRepository;
    private final LanguageTeacherMappingRepository languageTeacherMappingRepository;
    private final GroupTeacherMappingRepository groupTeacherMappingRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;
    private final GroupTeacherRepository groupTeacherRepository;
    private final LanguageRepository languageRepository;
    private final RequiredConstraintRepository requiredConstraintRepository;
    private final CustomConstraintRepository customConstraintRepository;
    private final ObjectMapper objectMapper;
    private final TimetablingProcessRepository timetablingProcessRepository;
    public static final Integer POPULATION_SIZE = 500;
    public static final Integer NUM_OF_CROSS = 50;
    public static final Integer NUM_LOOP = 1000;

    public static final Long TIME_LIMIT = 15 * 60 * 1000L;

    public TimetablingServiceHelper(TeacherRepository teacherRepository,
                                    LanguageTeacherMappingRepository languageTeacherMappingRepository,
                                    GroupTeacherMappingRepository groupTeacherMappingRepository,
                                    SubjectRepository subjectRepository,
                                    ClassRepository classRepository,
                                    GroupTeacherRepository groupTeacherRepository,
                                    LanguageRepository languageRepository,
                                    RequiredConstraintRepository requiredConstraintRepository,
                                    CustomConstraintRepository customConstraintRepository,
                                    ObjectMapper objectMapper,
                                    TimetablingProcessRepository timetablingProcessRepository) {
        this.teacherRepository = teacherRepository;
        this.languageTeacherMappingRepository = languageTeacherMappingRepository;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.subjectRepository = subjectRepository;
        this.classRepository = classRepository;
        this.groupTeacherRepository = groupTeacherRepository;
        this.languageRepository = languageRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.customConstraintRepository = customConstraintRepository;
        this.objectMapper = objectMapper;
        this.timetablingProcessRepository = timetablingProcessRepository;
    }

    @Async("async-thread-pool")
    public void timetablingTeacherAsync(TimetablingProcessEntity entity) throws JsonProcessingException {
        entity.setStatus("PROCESSING");
        entity.setUpdatedAt(new Date());
        timetablingProcessRepository.save(entity);

        try {
            InputData inputData = getInputData(entity.getDataset());
            Population population = initPopulation(inputData);

            Long startTime = Instant.now().toEpochMilli();
            for (int i = 0; i < NUM_LOOP; i++) {
                evaluateConstraint(inputData, population);
                if (i == NUM_LOOP - 1) {
                    Population.Member bestSolution = getTheMostObjectiveResult(population);
                    log.info("Solution: {}", objectMapper.writeValueAsString(bestSolution));
                    break;
                }
                selection(population);
                crossover(inputData, population);
                mutation(inputData, population);
                log.info("End loop {}", i);
                entity.setErrorMessage(String.valueOf(i));
                entity.setUpdatedAt(new Date());
                timetablingProcessRepository.save(entity);

                long now = Instant.now().toEpochMilli();

                if (now - startTime > TIME_LIMIT) {
                    Population.Member bestSolution = getTheMostObjectiveResult(population);
                    log.info("Solution: {}", objectMapper.writeValueAsString(bestSolution));
                    break;
                }
            }

            Population.Member bestSolution = getTheMostObjectiveResult(population);
            if (bestSolution != null) {
                bestSolution.setObjective(objectiveFunction(inputData, bestSolution));
            }
            log.info("Solution: {}", objectMapper.writeValueAsString(bestSolution));

            saveSolution(inputData.getClasses(), bestSolution);

            entity.setStatus("SUCCESS");
            entity.setUpdatedAt(new Date());
            timetablingProcessRepository.save(entity);
        } catch (Exception ex) {
            entity.setStatus("FAILED");
            entity.setErrorMessage(ex.getMessage());
            entity.setUpdatedAt(new Date());
            timetablingProcessRepository.save(entity);
        }
    }

    public void saveSolution(List<ClassEntity> classEntities, Population.Member member) {
        for (ClassEntity classEntity : classEntities) {
            for (Population.Member.MemberDetail detail : member.getDetails()) {
                if (classEntity.getId().equals(detail.getClassId())) {
                    classEntity.setIsAssigned(ClassConstant.ASSIGNED);
                    classEntity.setTeacherId(detail.getTeacherId());
                    classEntity.setUpdatedAt(new Date());
                }
            }
        }

        classRepository.saveAll(classEntities);
    }

    public Population.Member getTheMostObjectiveResult(Population population) {
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

    public InputData getInputData(Long dataset) {
        InputData inputData = new InputData();
        List<TeacherEntity> teachers = teacherRepository.findAllByStatusAndDataset(TeacherConstant.Status.ACTIVE, dataset);
        List<LanguageTeacherMappingEntity> languageTeacherMappings = languageTeacherMappingRepository.findByDataset(dataset);
        List<GroupTeacherMappingEntity> groupTeacherMappings = groupTeacherMappingRepository.findByDataset(dataset);
        List<SubjectEntity> subjects = subjectRepository.findByDataset(dataset);
        List<ClassEntity> classes = classRepository.findByDataset(dataset);
        List<GroupTeacherEntity> groupTeachers = groupTeacherRepository.findByDataset(dataset);
        List<LanguageEntity> languages = languageRepository.findAll();
        List<RequiredConstraintEntity> requiredConstraints = requiredConstraintRepository.findByStatusAndDataset(1, dataset);
        List<CustomConstraintEntity> customConstraints = customConstraintRepository.findByStatusAndDataset(1, dataset);

        inputData.setTeachers(teachers);
        inputData.setLanguageTeacherMappings(languageTeacherMappings);
        inputData.setGroupTeacherMappings(groupTeacherMappings);
        inputData.setSubjects(subjects);
        inputData.setClasses(classes);
        inputData.setGroupTeachers(groupTeachers);
        inputData.setLanguages(languages);
        inputData.setRequiredConstraints(requiredConstraints);
        inputData.setCustomConstraints(customConstraints);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfClasses(classes.size());
        inputData.setNumOfGroups(groupTeachers.size());
        inputData.setNumOfSubjects(subjects.size());
        inputData.setNumOfLanguages(languages.size());

        Double allTimeGdTeacher = teachers.stream().map(TeacherEntity::getGdTime).reduce(0d, Double::sum);
        Double allTimeClass = classes.stream().map(ClassEntity::getTimeOfClass).reduce(0d, Double::sum);

        if (allTimeGdTeacher <= 0) {
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }

        Double averageGD = allTimeClass / allTimeGdTeacher;
        inputData.setAverageGD(averageGD);

        List<TeacherClassMapping> teacherClassMappings = getTeacherClassMapping(inputData);
        inputData.setTeacherClassMappings(teacherClassMappings);
        return inputData;
    }

    public List<TeacherClassMapping> getTeacherClassMapping(InputData inputData) {
        List<TeacherEntity> teacherEntities = inputData.getTeachers();
        List<GroupTeacherMappingEntity> groupTeacherMappingEntities = inputData.getGroupTeacherMappings();
        List<SubjectEntity> subjectEntities = inputData.getSubjects();
        List<ClassEntity> classEntities = inputData.getClasses();

        List<TeacherClassMapping> teacherClassMappings = new ArrayList<>();
        for (TeacherEntity teacherEntity : teacherEntities) {
            for (GroupTeacherMappingEntity groupTeacherMappingEntity : groupTeacherMappingEntities) {
                if (groupTeacherMappingEntity.getTeacherId() != null && teacherEntity.getId().equals(groupTeacherMappingEntity.getTeacherId())) {
                    for (SubjectEntity subjectEntity : subjectEntities) {
                        if (subjectEntity.getGroupId() != null && groupTeacherMappingEntity.getGroupId() != null && subjectEntity.getGroupId().equals(groupTeacherMappingEntity.getGroupId())) {
                            for (ClassEntity classEntity : classEntities) {
                                if (classEntity.getSubjectId() != null && subjectEntity.getId().equals(classEntity.getSubjectId())) {
                                    for (LanguageTeacherMappingEntity languageTeacherMappingEntity : inputData.getLanguageTeacherMappings()) {
                                        if (languageTeacherMappingEntity.getTeacherId() != null &&
                                                languageTeacherMappingEntity.getTeacherId().equals(teacherEntity.getId()) &&
                                                languageTeacherMappingEntity.getLanguageId() != null &&
                                                languageTeacherMappingEntity.getLanguageId().equals(classEntity.getLanguageId())) {
                                            TeacherClassMapping teacherClassMapping = new TeacherClassMapping();
                                            teacherClassMapping.setClassId(classEntity.getId());
                                            teacherClassMapping.setTeacherId(teacherEntity.getId());
                                            teacherClassMappings.add(teacherClassMapping);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return teacherClassMappings;
    }

    public Population initPopulation(InputData inputData) {
        Population population = new Population();
        List<Population.Member> members = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Population.Member member = new Population.Member();
            List<Population.Member.MemberDetail> details = new ArrayList<>();
            for (ClassEntity classEntity : inputData.getClasses()) {
                Population.Member.MemberDetail detail = new Population.Member.MemberDetail();
                detail.setClassId(classEntity.getId());
                detail.setTeacherId(getRandomTeacherFromList(inputData.getTeachers(), inputData, classEntity).getId());
                details.add(detail);
            }
            member.setDetails(details);
            members.add(member);
        }
        population.setPopulation(members);
        return population;
    }

    public double objectiveFunction(InputData inputData, Population.Member member) {
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

    public void evaluateConstraint(InputData inputData, Population population) {
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

            for (ClassEntity classEntity : inputData.getClasses()) {
                int ct2 = 0;
                int ct3 = 0;
                for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                    if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {
                        if (!CollectionUtils.isEmpty(CT2)) {
                            ct2 += isTeacherClassMapping(teacherEntity, classEntity, inputData);
                        }

                        if (!CollectionUtils.isEmpty(CT3)) {
                            ct3 += 1;
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(CT2)) {
                    if (ct2 != 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }

                if (!CollectionUtils.isEmpty(CT3)) {
                    if (ct3 != 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                int ct4 = 0;
                int ct5 = 0;
                for (ClassEntity classEntity : inputData.getClasses()) {
                    if (isTeacherOfClass(member, teacherEntity, classEntity) == 1) {

                        if (!CollectionUtils.isEmpty(CT4)) {
                            ct4 += 1;
                        }

                        if (!CollectionUtils.isEmpty(CT5)) {
                            ct5 += classEntity.getTimeOfClass();
                        }
                    }


                    if (!CollectionUtils.isEmpty(CT6)) {
                        for (ClassEntity classEntity2 : inputData.getClasses()) {
                            if (classEntity.getId() < classEntity2.getId() && isTeacherOfClass(member, teacherEntity, classEntity) == 1 &&
                                    isTeacherOfClass(member, teacherEntity, classEntity2) == 1 &&
                                    isConflictClass(classEntity, classEntity2) == 1) {
                                member.setObjective(member.getObjective() + 100000);
                            }
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(CT4)) {
                    if (ct4 < 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }

                if (!CollectionUtils.isEmpty(CT5)) {
                    double maxTime = 1.2 * teacherEntity.getGdTime() * inputData.getAverageGD();
                    if (ct5 > maxTime) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }
        }
    }

    public int isTeacherClassMapping(TeacherEntity teacherEntity, ClassEntity classEntity, InputData inputData) {
        List<TeacherClassMapping> teacherClassMappings = inputData.getTeacherClassMappings().stream().filter(x -> (teacherEntity.getId().equals(x.getTeacherId()) && classEntity.getId().equals(x.getClassId()))).toList();
        if (CollectionUtils.isEmpty(teacherClassMappings)) {
            return 0;
        }
        return 1;
    }

    public void selection(Population population) {
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

    public void crossover(InputData inputData, Population population) {
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

    public void mutation(InputData inputData, Population population) {
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

    public void setTeacherForClass(Population.Member member, TeacherEntity teacherEntity, ClassEntity classEntity) {
        for (Population.Member.MemberDetail detail : member.getDetails()) {
            if (detail.getClassId().equals(classEntity.getId())) {
                detail.setTeacherId(teacherEntity.getId());
            }
        }
    }

    public int isConflictClass(ClassEntity class1, ClassEntity class2) {
        try {
            if (class1.equals(class2)) {
                return 0;
            }
            if (!StringUtils.isBlank(class1.getTimeOfDay()) && !StringUtils.isBlank(class2.getTimeOfDay())) {
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
            }

            if (class1.getStartTime() != null && class1.getEndTime() != null && class2.getStartTime() != null && class2.getEndTime() != null) {
                if (!StringUtils.isBlank(class1.getDayOfWeek()) && !StringUtils.isBlank(class2.getDayOfWeek()) && class1.getDayOfWeek().equals(class2.getDayOfWeek())) {
                    if (class1.getStartTime() < class2.getEndTime() || class1.getEndTime() > class2.getStartTime()) {
                        return 1;
                    }
                }

                if (!StringUtils.isBlank(class1.getBuilding()) && !StringUtils.isBlank(class2.getBuilding()) && !class1.getBuilding().equals(class2.getBuilding())) {
                    if (class1.getStartTime() - class2.getEndTime() > 0 && class1.getStartTime() - class2.getEndTime() < 15) {
                        return 1;
                    }
                    if (class2.getStartTime() - class1.getEndTime() > 0 && class2.getStartTime() - class1.getEndTime() < 15) {
                        return 1;
                    }
                }
                return 0;
            }
            return 0;
        } catch (Exception ex) {
            log.error("Check class conflict failed", ex);
            return 0;
        }
    }

    public void clearPopulationObjective(Population population) {
        for (Population.Member member : population.getPopulation()) {
            member.setObjective(0D);
        }
    }

    public int isTeacherOfClass(Population.Member member, TeacherEntity teacherEntity, ClassEntity classEntity) {
        List<Population.Member.MemberDetail> memberDetail = member.getDetails().stream()
                .filter(x -> (x.getTeacherId().equals(teacherEntity.getId()) && x.getClassId().equals(classEntity.getId()))).toList();

        return (CollectionUtils.isEmpty(memberDetail)) ? 0 : 1;
    }

    public int classHasLanguage(LanguageEntity languageEntity, ClassEntity classEntity) {
        return classEntity.getLanguageId().equals(languageEntity.getId()) ? 1 : 0;
    }

    public int teacherHasLanguage(LanguageEntity languageEntity, TeacherEntity teacherEntity, List<LanguageTeacherMappingEntity> ltMappings) {
        List<LanguageTeacherMappingEntity> ltMapping = ltMappings.stream()
                .filter(x -> x.getLanguageId().equals(languageEntity.getId()) && x.getTeacherId().equals(teacherEntity.getId()))
                .toList();

        return (CollectionUtils.isEmpty(ltMapping)) ? 0 : 1;
    }

    public TeacherEntity getRandomTeacherFromList(List<TeacherEntity> teachers, InputData inputData, ClassEntity classEntity) {
        Random rand = new Random();
        Long classId = classEntity.getId();
        List<TeacherClassMapping> teacherClassMappings = inputData.getTeacherClassMappings().stream().filter(x -> x.getClassId().equals(classId)).toList();
        if (CollectionUtils.isEmpty(teacherClassMappings)) {
            return teachers.get(rand.nextInt(teachers.size()));
        }
        List<Long> teacherIds = teacherClassMappings.stream().map(TeacherClassMapping::getTeacherId).toList();
        if (CollectionUtils.isEmpty(teacherIds)) {
            return teachers.get(rand.nextInt(teachers.size()));
        }

        List<TeacherEntity> availableTeachers = teachers.stream().filter(x -> teacherIds.contains(x.getId())).toList();
        return availableTeachers.get(rand.nextInt(availableTeachers.size()));
    }

}
