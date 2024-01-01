package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ClassConstant;
import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.model.geneticalgorithm.InputData;
import com.example.backendproject.model.geneticalgorithm.PopulationStudent;
import com.example.backendproject.repository.sc5.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class TimeTablingStudentServiceHelper {
    private final TeacherRepository teacherRepository;
    private final StudentProjectRepository studentProjectRepository;
    private final RequiredConstraintRepository requiredConstraintRepository;
    private final CustomConstraintRepository customConstraintRepository;
    private final ObjectMapper objectMapper;
    private final TimetablingProcessRepository timetablingProcessRepository;
    public static final Integer POPULATION_SIZE = 100;
    public static final Integer NUM_OF_CROSS = 20;
    public static final Integer NUM_LOOP = 200;

    public TimeTablingStudentServiceHelper(TeacherRepository teacherRepository,
                                           StudentProjectRepository studentProjectRepository,
                                           RequiredConstraintRepository requiredConstraintRepository,
                                           CustomConstraintRepository customConstraintRepository,
                                           ObjectMapper objectMapper,
                                           TimetablingProcessRepository timetablingProcessRepository) {
        this.teacherRepository = teacherRepository;
        this.studentProjectRepository = studentProjectRepository;
        this.requiredConstraintRepository = requiredConstraintRepository;
        this.customConstraintRepository = customConstraintRepository;
        this.objectMapper = objectMapper;
        this.timetablingProcessRepository = timetablingProcessRepository;
    }

    @Async("async-thread-pool")
    public void timetablingStudentAsync(TimetablingProcessEntity entity) throws JsonProcessingException {
        entity.setStatus("PROCESSING");
        entity.setUpdatedAt(new Date());
        timetablingProcessRepository.save(entity);

        try {
            InputData inputData = getInputData();
            PopulationStudent population = initPopulation(inputData);

            for (int i = 0; i < NUM_LOOP; i++) {
                evaluateConstraint(inputData, population);
                if (i == NUM_LOOP - 1) {
                    PopulationStudent.Member bestSolution = getTheMostObjectiveResult(population);
                    log.info("Solution: {}", objectMapper.writeValueAsString(bestSolution));
                    break;
                }
                selection(population);
                crossover(inputData, population);
                mutation(inputData, population);
                log.info("End loop {}", i);
            }

            PopulationStudent.Member bestSolution = getTheMostObjectiveResult(population);
            if (bestSolution != null) {
                bestSolution.setObjective(objectiveFunction(inputData, bestSolution));
            }
            log.info("Solution: {}", objectMapper.writeValueAsString(bestSolution));

            saveSolution(inputData.getStudentProjects(), bestSolution);

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

    private void saveSolution(List<StudentProjectEntity> studentProjectEntities, PopulationStudent.Member member) {
        for (StudentProjectEntity studentProjectEntity : studentProjectEntities) {
            for (PopulationStudent.Member.MemberDetail detail : member.getDetails()) {
                if (studentProjectEntity.getId().equals(detail.getStudentId())) {
                    studentProjectEntity.setIsAssigned(ClassConstant.ASSIGNED);
                    studentProjectEntity.setTeacherAssignedId(detail.getTeacherId());
                    studentProjectEntity.setUpdatedAt(new Date());
                }
            }
        }

        studentProjectRepository.saveAll(studentProjectEntities);
    }

    private InputData getInputData() {
        InputData inputData = new InputData();
        List<TeacherEntity> teachers = teacherRepository.findAllByStatus(TeacherConstant.Status.ACTIVE);
        List<StudentProjectEntity> studentProjects = studentProjectRepository.findAll();
        List<RequiredConstraintEntity> requiredConstraints = requiredConstraintRepository.findAll();
        List<CustomConstraintEntity> customConstraints = customConstraintRepository.findAll();

        inputData.setTeachers(teachers);
        inputData.setStudentProjects(studentProjects);
        inputData.setRequiredConstraints(requiredConstraints);
        inputData.setCustomConstraints(customConstraints);

        inputData.setNumOfTeachers(teachers.size());
        inputData.setNumOfStudents(studentProjects.size());

        int allTimeHdTeacher = teachers.stream().map(TeacherEntity::getHdTime).reduce(0, Integer::sum);
        Integer allTimeHd = studentProjects.stream().map(StudentProjectEntity::getTimeHd).reduce(0, Integer::sum);

        if (allTimeHdTeacher <= 0) {
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }

        Double averageHD = (double) allTimeHd / allTimeHdTeacher;
        inputData.setAverageHD(averageHD);

        return inputData;
    }

    private PopulationStudent initPopulation(InputData inputData) {
        PopulationStudent population = new PopulationStudent();
        List<PopulationStudent.Member> members = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            PopulationStudent.Member member = new PopulationStudent.Member();
            List<PopulationStudent.Member.MemberDetail> details = new ArrayList<>();
            for (StudentProjectEntity studentProjectEntity : inputData.getStudentProjects()) {
                PopulationStudent.Member.MemberDetail detail = new PopulationStudent.Member.MemberDetail();
                detail.setStudentId(studentProjectEntity.getId());
                detail.setTeacherId(getRandomTeacherFromList(inputData.getTeachers(), studentProjectEntity));
                details.add(detail);
            }
            member.setDetails(details);
            members.add(member);
        }
        population.setPopulation(members);
        return population;
    }

    private void evaluateConstraint(InputData inputData, PopulationStudent population) {
        clearPopulationObjective(population);
        List<RequiredConstraintEntity> requiredConstraints = inputData.getRequiredConstraints();
        List<RequiredConstraintEntity> CT7 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT7") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT8 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT8") && x.getStatus().equals(1)).toList();
        List<RequiredConstraintEntity> CT9 = requiredConstraints.stream().filter(x -> x.getCode().equals("CT9") && x.getStatus().equals(1)).toList();

        for (PopulationStudent.Member member : population.getPopulation()) {
            log.info("Start member {}", member);
            member.setObjective(objectiveFunction(inputData, member));

            // CT7: 1 student is only assigned by 1 teacher
            if (!CollectionUtils.isEmpty(CT7)) {
                for (StudentProjectEntity studentProjectEntity : inputData.getStudentProjects()) {
                    int ct7 = 0;
                    for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                        if (isTeacherOfStudent(member, teacherEntity, studentProjectEntity) == 1) {
                            ct7 += 1;
                        }
                    }
                    if (ct7 != 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            // CT8: Each teacher has at least 1 student
            if (!CollectionUtils.isEmpty(CT8)) {
                for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                    int ct8 = 0;
                    for (StudentProjectEntity studentProjectEntity : inputData.getStudentProjects()) {
                        if (isTeacherOfStudent(member, teacherEntity, studentProjectEntity) == 1) {
                            ct8 += 1;
                        }
                    }
                    if (ct8 < 1) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }

            // CT9: Số giờ được phân công cho 1 GV không được vượt quá 120% * hdTime của GV * averageHD
            if (!CollectionUtils.isEmpty(CT9)) {
                for (TeacherEntity teacherEntity : inputData.getTeachers()) {
                    int ct9 = 0;
                    for (StudentProjectEntity studentProjectEntity : inputData.getStudentProjects()) {
                        if (isTeacherOfStudent(member, teacherEntity, studentProjectEntity) == 1) {
                            ct9 += studentProjectEntity.getTimeHd();
                        }
                    }
                    double maxTime = 1.2 * teacherEntity.getHdTime() * inputData.getAverageHD();
                    if (ct9 > maxTime) {
                        member.setObjective(member.getObjective() + 100000);
                    }
                }
            }
        }
    }

    private double objectiveFunction(InputData inputData, PopulationStudent.Member member) {
        double objective = 0;
        for (TeacherEntity teacherEntity : inputData.getTeachers()) {
            int timeTraining = 0;
            for (StudentProjectEntity studentProjectEntity : inputData.getStudentProjects()) {
                if (isTeacherOfStudent(member, teacherEntity, studentProjectEntity) == 1) {
                    timeTraining += studentProjectEntity.getTimeHd();
                }
            }
            double diff = Math.abs(inputData.getAverageHD() * teacherEntity.getHdTime() - timeTraining);
            objective = Math.max(objective, diff);
        }
        return objective;
    }

    private PopulationStudent.Member getTheMostObjectiveResult(PopulationStudent population) {
        List<PopulationStudent.Member> listMember = population.getPopulation();
        List<Double> objectiveTemp = new ArrayList<>(listMember.stream().map(PopulationStudent.Member::getObjective).toList());
        Collections.sort(objectiveTemp);

        Double mostObjective = objectiveTemp.get(0);
        for (PopulationStudent.Member member : listMember) {
            if (member.getObjective().equals(mostObjective)) {
                return member;
            }
        }

        return null;
    }

    private void selection(PopulationStudent population) {
        List<PopulationStudent.Member> listMember = population.getPopulation();
        List<Double> objectiveTemp = new ArrayList<>(listMember.stream().map(PopulationStudent.Member::getObjective).toList());
        Collections.sort(objectiveTemp);

        int limitIndex = listMember.size() * 80 / 100;
        Double objectiveLimit = objectiveTemp.get(limitIndex);

        for (PopulationStudent.Member member : listMember) {
            if (member.getObjective() > objectiveLimit) {
                Random rand = new Random();
                member.setDetails(listMember.get(rand.nextInt(listMember.size())).getDetails());
            }
        }
    }

    private void crossover(InputData inputData, PopulationStudent population) {
        List<PopulationStudent.Member> listMember = population.getPopulation();
        for (int i = 0; i < NUM_OF_CROSS; i++) {
            Random rand = new Random();
            int father = rand.nextInt(listMember.size());
            int mother = rand.nextInt(listMember.size());
            for (int j = 0; j < inputData.getNumOfStudents(); j++) {
                // keep 1/2 father and replace 1/2 father by mother and same mother
                if (j % 2 == 0) {
                    for (TeacherEntity teacher1 : inputData.getTeachers()) {
                        for (TeacherEntity teacher2 : inputData.getTeachers()) {
                            if (isTeacherOfStudent(listMember.get(father), teacher1, inputData.getStudentProjects().get(j)) == 1 &&
                                    isTeacherOfStudent(listMember.get(mother), teacher2, inputData.getStudentProjects().get(j)) == 1) {
                                setTeacherForStudent(listMember.get(father), teacher2, inputData.getStudentProjects().get(j));
                                setTeacherForStudent(listMember.get(mother), teacher1, inputData.getStudentProjects().get(j));
                            }
                        }
                    }
                }
            }
        }
    }

    private void mutation(InputData inputData, PopulationStudent population) {
        Random rand = new Random();
        PopulationStudent.Member member = population.getPopulation().get(rand.nextInt(population.getPopulation().size()));

        StudentProjectEntity student1 = inputData.getStudentProjects().get(rand.nextInt(inputData.getNumOfStudents()));
        List<StudentProjectEntity> newList = inputData.getStudentProjects().stream().filter(x -> !x.getId().equals(student1.getId())).toList();
        StudentProjectEntity student2 = newList.get(rand.nextInt(inputData.getNumOfStudents() - 1));

        for (TeacherEntity teacher1 : inputData.getTeachers()) {
            for (TeacherEntity teacher2 : inputData.getTeachers()) {
                if (isTeacherOfStudent(member, teacher1, student1) == 1 && isTeacherOfStudent(member, teacher2, student2) == 1) {
                    setTeacherForStudent(member, teacher1, student2);
                    setTeacherForStudent(member, teacher2, student1);
                }
            }
        }
    }

    private void setTeacherForStudent(PopulationStudent.Member member, TeacherEntity teacherEntity, StudentProjectEntity studentProjectEntity) {
        for (PopulationStudent.Member.MemberDetail detail : member.getDetails()) {
            if (detail.getStudentId().equals(studentProjectEntity.getId())) {
                detail.setTeacherId(teacherEntity.getId());
            }
        }
    }

    private int isTeacherOfStudent(PopulationStudent.Member member, TeacherEntity teacherEntity, StudentProjectEntity studentProjectEntity) {
        List<PopulationStudent.Member.MemberDetail> memberDetail = member.getDetails().stream()
                .filter(x -> (x.getTeacherId().equals(teacherEntity.getId()) && x.getStudentId().equals(studentProjectEntity.getId()))).toList();

        return (CollectionUtils.isEmpty(memberDetail)) ? 0 : 1;
    }

    private void clearPopulationObjective(PopulationStudent population) {
        for (PopulationStudent.Member member : population.getPopulation()) {
            member.setObjective(0D);
        }
    }

    private Long getRandomTeacherFromList(List<TeacherEntity> teachers, StudentProjectEntity studentProjectEntity) {
        Random rand = new Random();
        if (studentProjectEntity.getTeacher1Id() == null) {
            return teachers.get(rand.nextInt(teachers.size())).getId();
        }

        if (studentProjectEntity.getTeacher2Id() == null) {
            return studentProjectEntity.getTeacher1Id();
        }

        if (studentProjectEntity.getTeacher3Id() == null) {
            int i = rand.nextInt(2);
            return (i == 0) ? studentProjectEntity.getTeacher1Id() : studentProjectEntity.getTeacher2Id();
        }

        int i = rand.nextInt(3);
        if (i == 0) {
            return studentProjectEntity.getTeacher1Id();
        }
        if (i == 1) {
            return studentProjectEntity.getTeacher2Id();
        }
        return studentProjectEntity.getTeacher3Id();
    }
}
