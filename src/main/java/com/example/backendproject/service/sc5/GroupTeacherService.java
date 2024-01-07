package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.GroupTeacherMappingConstant;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.GroupTeacherMappingMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.GroupTeacherMappingRepository;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.service.sc5.helper.GroupTeacherServiceHelper;
import com.example.backendproject.util.CommonUtil;
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
public class GroupTeacherService {
    private final GroupTeacherRepository groupTeacherRepository;
    private final AdminLogService adminLogService;
    private final GroupTeacherMapper groupTeacherMapper;
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final GroupTeacherMappingRepository groupTeacherMappingRepository;
    private final GroupTeacherMappingMapper groupTeacherMappingMapper;
    private final GroupTeacherServiceHelper groupTeacherServiceHelper;

    public GroupTeacherService(GroupTeacherRepository groupTeacherRepository,
                               AdminLogService adminLogService,
                               GroupTeacherMapper groupTeacherMapper,
                               TeacherRepository teacherRepository,
                               TeacherMapper teacherMapper,
                               GroupTeacherMappingRepository groupTeacherMappingRepository,
                               GroupTeacherMappingMapper groupTeacherMappingMapper,
                               GroupTeacherServiceHelper groupTeacherServiceHelper) {
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMapper = groupTeacherMapper;
        this.adminLogService = adminLogService;
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.groupTeacherMappingMapper = groupTeacherMappingMapper;
        this.groupTeacherServiceHelper = groupTeacherServiceHelper;
    }

    public GroupTeacherSearchResponse searchGroupTeacher(GroupTeacherSearchRequest request) {
        GroupTeacherSearchResponse response = new GroupTeacherSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        if (request.getDataset() == null) {
            response.setData(new ArrayList<>());
            return response;
        }

        List<GroupTeacher> data = groupTeacherRepository.searchGroupTeacherByFilter(request);
        for (GroupTeacher groupTeacher : data) {
            if (groupTeacher.getLeader() != null) {
                Optional<TeacherEntity> teacherEntity = teacherRepository.findById(groupTeacher.getLeader());
                teacherEntity.ifPresent(entity -> groupTeacher.setLeaderInfo(teacherMapper.toDto(entity)));
            }
        }
        response.setData(data);
        return response;
    }

    public void createGroupTeacher(GroupTeacher groupTeacher) {
        adminLogService.log("createGroupTeacher", CommonUtil.toJson(groupTeacher));
        log.info("Create group teacher with data: " + CommonUtil.toJson(groupTeacher));

        validateCreateGroupTeacherRequest(groupTeacher);
        GroupTeacherEntity groupTeacherEntity = groupTeacherMapper.toEntity(groupTeacher);
        groupTeacherEntity.setCreatedAt(new Date());
        groupTeacherEntity.setUpdatedAt(new Date());
        try {
            groupTeacherRepository.save(groupTeacherEntity);
        } catch (Exception exception) {
            log.error("Save group teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateGroupTeacherRequest(GroupTeacher groupTeacher) {
        if (StringUtils.isBlank(groupTeacher.getName()) || groupTeacher.getDataset() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin nhóm chuyên môn.");
        }
    }

    private void validateCreateGroupTeacherRequestUpload(GroupTeacherUpload groupTeacher) {
        if (StringUtils.isBlank(groupTeacher.getName())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên nhóm chuyên môn.");
        }
    }

    public void updateGroupTeacher(GroupTeacher groupTeacher) {
        if (groupTeacher.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy nhóm chuyên môn.");
        }
        validateCreateGroupTeacherRequest(groupTeacher);

        Optional<GroupTeacherEntity> groupTeacherEntityOptional = groupTeacherRepository.findById(groupTeacher.getId());
        if (groupTeacherEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy nhóm chuyên môn.");
        }

        GroupTeacherEntity groupTeacherEntity = groupTeacherEntityOptional.get();
        groupTeacherEntity.setName(groupTeacher.getName());
        groupTeacherEntity.setDescription(groupTeacher.getDescription());
        groupTeacherEntity.setLeader(groupTeacher.getLeader());
        groupTeacherEntity.setDataset(groupTeacher.getDataset());
        groupTeacherEntity.setUpdatedAt(new Date());

        try {
            groupTeacherRepository.save(groupTeacherEntity);
        } catch (Exception exception) {
            log.error("Update group teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileGroupTeacher(UploadGroupTeacherRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getGroupTeacherCreateRequests()) || request.getDataset() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (GroupTeacherUpload groupTeacherUpload : request.getGroupTeacherCreateRequests()) {
            validateCreateGroupTeacherRequestUpload(groupTeacherUpload);
        }

        groupTeacherServiceHelper.uploadFileGroupTeacher(request);
    }

    public GroupTeacherSearchResponse getAllGroupTeacher(Long dataset) {
        if (dataset == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }
        GroupTeacherSearchResponse response = new GroupTeacherSearchResponse();
        List<GroupTeacherEntity> allGroup = groupTeacherRepository.findByDataset(dataset);

        response.setData(groupTeacherMapper.toDtos(allGroup));
        return response;
    }

    public GroupTeacherDetail getGroupTeacherDetail(Long id) {
        if (id == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin nhóm chuyên môn");
        }

        Optional<GroupTeacherEntity> groupTeacherEntityOptional = groupTeacherRepository.findById(id);
        if (groupTeacherEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy thông tin nhóm chuyên môn");
        }

        GroupTeacherEntity entity = groupTeacherEntityOptional.get();
        GroupTeacherDetail groupTeacherDetail = new GroupTeacherDetail();
        groupTeacherDetail.setId(entity.getId());
        groupTeacherDetail.setName(entity.getName());
        groupTeacherDetail.setDescription(entity.getDescription());
        groupTeacherDetail.setCreatedAt(entity.getCreatedAt());
        groupTeacherDetail.setUpdatedAt(entity.getUpdatedAt());
        groupTeacherDetail.setLeader(entity.getLeader());
        groupTeacherDetail.setDataset(entity.getDataset());

        if (groupTeacherDetail.getLeader() != null) {
            Optional<TeacherEntity> teacherEntity = teacherRepository.findById(groupTeacherDetail.getLeader());
            teacherEntity.ifPresent(e -> groupTeacherDetail.setLeaderInfo(teacherMapper.toDto(e)));
        }

        List<Teacher> members = teacherRepository.getAllTeacherOfAGroupAndDataset(groupTeacherDetail.getId(), groupTeacherDetail.getDataset());
        groupTeacherDetail.setMembers(members);
        return groupTeacherDetail;
    }

    public void addTeacherToGroup(AddTeacherToGroupRequest request) {
        if (request.getTeacherId() == null || request.getGroupId() == null || StringUtils.isBlank(request.getRole()) || request.getDataset() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        if (!GroupTeacherMappingConstant.Role.LIST_ROLE.contains(request.getRole())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Vai trò không hợp lệ");
        }

        List<GroupTeacherMappingEntity> existed = groupTeacherMappingRepository.findByGroupIdAndTeacherIdAndDataset(request.getGroupId(), request.getTeacherId(), request.getDataset());
        if (!CollectionUtils.isEmpty(existed)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Giảng viên đã nằm trong nhóm");
        }

        GroupTeacherMappingEntity entity = new GroupTeacherMappingEntity();
        entity.setGroupId(request.getGroupId());
        entity.setTeacherId(request.getTeacherId());
        entity.setRole(request.getRole());
        entity.setDataset(request.getDataset());

        groupTeacherMappingRepository.save(entity);
    }

    public void updateTeacherToGroup(AddTeacherToGroupRequest request) {
        if (request.getTeacherId() == null || request.getGroupId() == null || StringUtils.isBlank(request.getRole())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        if (!GroupTeacherMappingConstant.Role.LIST_ROLE.contains(request.getRole())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Vai trò không hợp lệ");
        }

        List<GroupTeacherMappingEntity> existed = groupTeacherMappingRepository.findByGroupIdAndTeacherIdAndDataset(request.getGroupId(), request.getTeacherId(), request.getDataset());
        if (CollectionUtils.isEmpty(existed)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Giảng viên không nằm trong nhóm");
        }
        GroupTeacherMappingEntity entity = existed.get(0);
        entity.setRole(request.getRole());

        groupTeacherMappingRepository.save(entity);
    }

    public void uploadExcelGroupTeacherMapping(UploadGroupTeacherMappingRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getGroupTeacherMappingCreateRequests()) || request.getDataset() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (GroupTeacherMappingUpload groupTeacherMapping : request.getGroupTeacherMappingCreateRequests()) {
            if (StringUtils.isBlank(groupTeacherMapping.getTeacherName()) || StringUtils.isBlank(groupTeacherMapping.getGroupName()) || StringUtils.isBlank(groupTeacherMapping.getRole())) {
                throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
            }

            if (!GroupTeacherMappingConstant.Role.LIST_ROLE.contains(groupTeacherMapping.getRole())) {
                throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Vai trò không hợp lệ");
            }
        }

        groupTeacherServiceHelper.uploadExcelGroupTeacherMapping(request);
    }
}
