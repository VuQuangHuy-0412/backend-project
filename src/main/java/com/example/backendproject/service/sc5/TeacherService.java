package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.GroupTeacherMappingConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.GroupTeacherMappingRepository;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final AdminLogService adminLogService;
    private final TeacherMapper teacherMapper;
    private final GroupTeacherMappingRepository groupTeacherMappingRepository;
    private final GroupTeacherRepository groupTeacherRepository;
    private final GroupTeacherMapper groupTeacherMapper;

    public TeacherService(TeacherRepository teacherRepository,
                          AdminLogService adminLogService,
                          TeacherMapper teacherMapper,
                          GroupTeacherMappingRepository groupTeacherMappingRepository,
                          GroupTeacherRepository groupTeacherRepository,
                          GroupTeacherMapper groupTeacherMapper) {
        this.teacherRepository = teacherRepository;
        this.adminLogService = adminLogService;
        this.teacherMapper = teacherMapper;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMapper = groupTeacherMapper;
    }

    public TeacherSearchResponse searchTeacher(TeacherSearchRequest request) {
        TeacherSearchResponse response = new TeacherSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        if (request.getGroupTeacher() != null) {
            List<GroupTeacherMappingEntity> groupTeacherMappingEntities = groupTeacherMappingRepository.findAllByGroupId(request.getGroupTeacher());
            if (CollectionUtils.isEmpty(groupTeacherMappingEntities)) {
                response.setData(new ArrayList<>());
                return response;
            }

            request.setIds(groupTeacherMappingEntities.stream().map(GroupTeacherMappingEntity::getTeacherId).toList());
        }

        List<Teacher> data = teacherRepository.searchTeacherByFilter(request);
        for (Teacher teacher : data) {
            List<GroupTeacherMappingEntity> mappingEntities = groupTeacherMappingRepository.findAllByTeacherId(teacher.getId());
            List<Long> groupIds = mappingEntities.stream().map(GroupTeacherMappingEntity::getGroupId).toList();
            List<GroupTeacherEntity> groupTeacherEntities = groupTeacherRepository.findAllByIdIn(groupIds);

            teacher.setGroupTeacher(groupTeacherMapper.toDtos(groupTeacherEntities));
        }
        response.setData(data);
        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    public void createTeacher(Teacher teacher) {
        adminLogService.log("createTeacher", CommonUtil.toJson(teacher));
        log.info("Create teacher with data: " + CommonUtil.toJson(teacher));

        validateCreateTeacherRequest(teacher);
        TeacherEntity teacherEntity = teacherMapper.toEntity(teacher);
        teacherEntity.setCreatedAt(new Date());
        teacherEntity.setUpdatedAt(new Date());

        try {
            teacherEntity = teacherRepository.save(teacherEntity);
            if (!CollectionUtils.isEmpty(teacher.getGroupTeacher())) {
                List<GroupTeacherMappingEntity> mappingEntities = getGroupTeacherMappingEntities(teacher, teacherEntity);
                if (!CollectionUtils.isEmpty(mappingEntities)) {
                    groupTeacherMappingRepository.saveAll(mappingEntities);
                }
            }

        } catch (Exception exception) {
            log.error("Save teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private static List<GroupTeacherMappingEntity> getGroupTeacherMappingEntities(Teacher teacher, TeacherEntity teacherEntity) {
        List<GroupTeacherMappingEntity> mappingEntities = new ArrayList<>();
        for (GroupTeacher groupTeacher : teacher.getGroupTeacher()) {
            GroupTeacherMappingEntity entity = new GroupTeacherMappingEntity();
            entity.setTeacherId(teacherEntity.getId());
            entity.setGroupId(groupTeacher.getId());
            entity.setRole(GroupTeacherMappingConstant.Role.TEACHER);
            mappingEntities.add(entity);
        }
        return mappingEntities;
    }

    private void validateCreateTeacherRequest(Teacher teacher) {
        if (StringUtils.isBlank(teacher.getFullName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên giảng viên.");
        }

        if (teacher.getStartTime() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thời điểm bắt đầu làm việc.");
        }

        if (teacher.getBirthday() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin ngày sinh.");
        }
    }

    public void updateTeacher(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }
        validateCreateTeacherRequest(teacher);

        Optional<TeacherEntity> teacherEntityOptional = teacherRepository.findById(teacher.getId());
        if (teacherEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }

        TeacherEntity teacherEntity = teacherEntityOptional.get();
        teacherEntity.setFullName(teacher.getFullName());
        teacherEntity.setRankAndDegree(teacher.getRankAndDegree());
        teacherEntity.setStartTime(teacher.getStartTime());
        teacherEntity.setBirthday(teacher.getBirthday());
        teacherEntity.setGdTime(teacher.getGdTime());
        teacherEntity.setHdTime(teacher.getHdTime());
        teacherEntity.setRating(teacher.getRating());
        teacherEntity.setUpdatedAt(new Date());

        try {
            teacherRepository.save(teacherEntity);
        } catch (Exception exception) {
            log.error("Update teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileTeacher(UploadTeacherRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getTeacherCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (Teacher teacher : request.getTeacherCreateRequests()) {
            validateCreateTeacherRequest(teacher);
        }

        List<TeacherEntity> entities = teacherMapper.toEntities(request.getTeacherCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        teacherRepository.saveAll(entities);
    }

    public TeacherSearchResponse getAllTeacherByGroup(Long groupId) {
        if (groupId == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        Optional<GroupTeacherEntity> groupTeacherEntity = groupTeacherRepository.findById(groupId);
        if (groupTeacherEntity.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin nhóm chuyên môn");
        }

        TeacherSearchResponse response = new TeacherSearchResponse();
        List<GroupTeacherMappingEntity> mappingEntities = groupTeacherMappingRepository.findAllByGroupId(groupId);
        if (CollectionUtils.isEmpty(mappingEntities)) {
            response.setData(new ArrayList<>());
            return response;
        }

        List<Long> teacherIds = mappingEntities.stream().map(GroupTeacherMappingEntity::getTeacherId).toList();
        List<TeacherEntity> teachers = teacherRepository.findAllById(teacherIds);

        response.setData(teacherMapper.toDtos(teachers));
        return response;
    }

    public TeacherSearchResponse getAllTeacher() {
        TeacherSearchResponse response = new TeacherSearchResponse();
        List<TeacherEntity> entities = teacherRepository.findAll();

        response.setData(teacherMapper.toDtos(entities));
        return response;
    }
}
