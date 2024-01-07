package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.GroupTeacherMappingConstant;
import com.example.backendproject.config.constant.TeacherConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.*;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.LanguageTeacherMappingMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.*;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.service.sc5.helper.TeacherServiceHelper;
import com.example.backendproject.util.CommonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final AdminLogService adminLogService;
    private final TeacherMapper teacherMapper;
    private final GroupTeacherMappingRepository groupTeacherMappingRepository;
    private final LanguageTeacherMappingRepository languageTeacherMappingRepository;
    private final LanguageTeacherMappingMapper languageTeacherMappingMapper;
    private final GroupTeacherRepository groupTeacherRepository;
    private final GroupTeacherMapper groupTeacherMapper;
    private final TeacherServiceHelper teacherServiceHelper;
    private final ClassRepository classRepository;
    private final StudentProjectRepository studentProjectRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          AdminLogService adminLogService,
                          TeacherMapper teacherMapper,
                          GroupTeacherMappingRepository groupTeacherMappingRepository,
                          LanguageTeacherMappingRepository languageTeacherMappingRepository,
                          LanguageTeacherMappingMapper languageTeacherMappingMapper,
                          GroupTeacherRepository groupTeacherRepository,
                          GroupTeacherMapper groupTeacherMapper,
                          TeacherServiceHelper teacherServiceHelper,
                          ClassRepository classRepository,
                          StudentProjectRepository studentProjectRepository) {
        this.teacherRepository = teacherRepository;
        this.adminLogService = adminLogService;
        this.teacherMapper = teacherMapper;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.languageTeacherMappingRepository = languageTeacherMappingRepository;
        this.languageTeacherMappingMapper = languageTeacherMappingMapper;
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMapper = groupTeacherMapper;
        this.teacherServiceHelper = teacherServiceHelper;
        this.classRepository = classRepository;
        this.studentProjectRepository = studentProjectRepository;
    }

    public TeacherSearchResponse searchTeacher(TeacherSearchRequest request) {
        TeacherSearchResponse response = new TeacherSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        if (request.getGroupTeacher() != null) {
            List<GroupTeacherMappingEntity> groupTeacherMappingEntities = groupTeacherMappingRepository.findAllByGroupIdAndDataset(request.getGroupTeacher(), request.getDataset());
            if (CollectionUtils.isEmpty(groupTeacherMappingEntities)) {
                response.setData(new ArrayList<>());
                return response;
            }

            request.setIds(groupTeacherMappingEntities.stream().map(GroupTeacherMappingEntity::getTeacherId).toList());
        }

        List<Teacher> data = teacherRepository.searchTeacherByFilter(request);
        for (Teacher teacher : data) {
            List<GroupTeacherMappingEntity> mappingEntities = groupTeacherMappingRepository.findAllByTeacherIdAndDataset(teacher.getId(), request.getDataset());
            List<Long> groupIds = mappingEntities.stream().map(GroupTeacherMappingEntity::getGroupId).toList();
            List<GroupTeacherEntity> groupTeacherEntities = groupTeacherRepository.findAllByIdInAndDataset(groupIds, request.getDataset());

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
            teacherRepository.save(teacherEntity);
        } catch (Exception exception) {
            log.error("Save teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
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

        if (teacher.getStatus() == null || !TeacherConstant.Status.LIST_STATUS_VALID.contains(teacher.getStatus())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Trạng thái không hợp lệ.");
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
        teacherEntity.setStatus(teacher.getStatus());
        teacherEntity.setTotalTime(teacher.getTotalTime());
        teacherEntity.setDataset(teacher.getDataset());
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

        for (TeacherUpload teacher : request.getTeacherCreateRequests()) {
            validateCreateTeacherRequestUpload(teacher);
        }

        teacherServiceHelper.uploadFileTeacher(request);
    }

    private void validateCreateTeacherRequestUpload(TeacherUpload teacher) {
        if (StringUtils.isBlank(teacher.getFullName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên giảng viên.");
        }

        if (teacher.getStatus() == null || !TeacherConstant.Status.LIST_STATUS_VALID.contains(teacher.getStatus())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Trạng thái không hợp lệ.");
        }
    }

    public TeacherSearchResponse getAllTeacherByGroup(Long groupId, Long dataset) {
        if (groupId == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        Optional<GroupTeacherEntity> groupTeacherEntity = groupTeacherRepository.findById(groupId);
        if (groupTeacherEntity.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin nhóm chuyên môn");
        }

        TeacherSearchResponse response = new TeacherSearchResponse();
        List<GroupTeacherMappingEntity> mappingEntities = groupTeacherMappingRepository.findAllByGroupIdAndDataset(groupId, dataset);
        if (CollectionUtils.isEmpty(mappingEntities)) {
            response.setData(new ArrayList<>());
            return response;
        }

        List<Long> teacherIds = mappingEntities.stream().map(GroupTeacherMappingEntity::getTeacherId).toList();
        List<TeacherEntity> teachers = teacherRepository.findAllById(teacherIds);

        response.setData(teacherMapper.toDtos(teachers));
        return response;
    }

    public TeacherSearchResponse getAllTeacher(Long dataset) {
        TeacherSearchResponse response = new TeacherSearchResponse();
        List<TeacherEntity> entities = teacherRepository.findByDataset(dataset);

        response.setData(teacherMapper.toDtos(entities));
        return response;
    }

    public void uploadFileLanguageTeacherMapping(UploadLanguageTeacherRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getLanguageTeacherCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (LanguageTeacherMappingUpload languageTeacherMapping : request.getLanguageTeacherCreateRequests()) {
            if (languageTeacherMapping.getTeacherName() == null || languageTeacherMapping.getLanguageName() == null) {
                throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
            }
        }

        teacherServiceHelper.uploadFileLanguageTeacherMapping(request);
    }

    public void calculateTimeTeacher(Long dataset) {
        List<ClassEntity> classEntities = classRepository.findByDataset(dataset);
        List<StudentProjectEntity> studentProjectEntities = studentProjectRepository.findByDataset(dataset);
        List<TeacherEntity> teacherEntities = teacherRepository.findByDataset(dataset);

        if (CollectionUtils.isEmpty(classEntities) || CollectionUtils.isEmpty(studentProjectEntities) || CollectionUtils.isEmpty(teacherEntities)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Bộ dữ liệu không hợp lệ");
        }

        double allTimeGd = classEntities.stream().map(ClassEntity::getTimeOfClass).reduce(0d, Double::sum);
        double allTimeHd = studentProjectEntities.stream().map(StudentProjectEntity::getTimeHd).reduce(0d, Double::sum);

        double rateGd = allTimeGd/(allTimeHd + allTimeGd);
        double rateHd = allTimeHd/(allTimeHd + allTimeGd);
        for (TeacherEntity teacherEntity : teacherEntities) {
            teacherEntity.setGdTime(rateGd * teacherEntity.getTotalTime());
            teacherEntity.setHdTime(rateHd * teacherEntity.getTotalTime());
        }
    }
}
