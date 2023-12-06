package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.GroupTeacher;
import com.example.backendproject.model.sc5.GroupTeacherSearchRequest;
import com.example.backendproject.model.sc5.GroupTeacherSearchResponse;
import com.example.backendproject.model.sc5.UploadGroupTeacherRequest;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    public GroupTeacherService(GroupTeacherRepository groupTeacherRepository,
                               AdminLogService adminLogService,
                               GroupTeacherMapper groupTeacherMapper,
                               TeacherRepository teacherRepository,
                               TeacherMapper teacherMapper) {
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMapper = groupTeacherMapper;
        this.adminLogService = adminLogService;
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    public GroupTeacherSearchResponse searchGroupTeacher(GroupTeacherSearchRequest request) {
        GroupTeacherSearchResponse response = new GroupTeacherSearchResponse();
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

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
        groupTeacherEntity.setUpdatedAt(new Date());

        try {
            groupTeacherRepository.save(groupTeacherEntity);
        } catch (Exception exception) {
            log.error("Update group teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileGroupTeacher(UploadGroupTeacherRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getGroupTeacherCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (GroupTeacher teacher : request.getGroupTeacherCreateRequests()) {
            validateCreateGroupTeacherRequest(teacher);
        }

        List<GroupTeacherEntity> entities = groupTeacherMapper.toEntities(request.getGroupTeacherCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        groupTeacherRepository.saveAll(entities);
    }

    public GroupTeacherSearchResponse getAllGroupTeacher() {
        GroupTeacherSearchResponse response = new GroupTeacherSearchResponse();
        List<GroupTeacherEntity> allGroup = groupTeacherRepository.findAll();

        response.setData(groupTeacherMapper.toDtos(allGroup));
        return response;
    }
}
