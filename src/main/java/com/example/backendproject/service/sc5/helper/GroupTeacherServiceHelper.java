package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.GroupTeacherMappingMapper;
import com.example.backendproject.model.sc5.*;
import com.example.backendproject.repository.sc5.GroupTeacherMappingRepository;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import com.example.backendproject.repository.sc5.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GroupTeacherServiceHelper {
    private final GroupTeacherMapper groupTeacherMapper;
    private final GroupTeacherRepository groupTeacherRepository;
    private final GroupTeacherMappingRepository groupTeacherMappingRepository;
    private final GroupTeacherMappingMapper groupTeacherMappingMapper;
    private final TeacherRepository teacherRepository;

    public GroupTeacherServiceHelper(GroupTeacherMapper groupTeacherMapper,
                                     GroupTeacherRepository groupTeacherRepository,
                                     GroupTeacherMappingRepository groupTeacherMappingRepository,
                                     GroupTeacherMappingMapper groupTeacherMappingMapper,
                                     TeacherRepository teacherRepository) {
        this.groupTeacherMapper = groupTeacherMapper;
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.groupTeacherMappingMapper = groupTeacherMappingMapper;
        this.teacherRepository = teacherRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileGroupTeacher(UploadGroupTeacherRequest request) {
        List<GroupTeacherEntity> entities = new ArrayList<>();

        for (GroupTeacherUpload groupTeacherUpload : request.getGroupTeacherCreateRequests()) {
            GroupTeacherEntity entity = new GroupTeacherEntity();
            entity.setName(groupTeacherUpload.getName());
            entity.setDescription(groupTeacherUpload.getDescription());
            List<TeacherEntity> teacherEntity = teacherRepository.findByFullName(groupTeacherUpload.getLeaderName());
            entity.setLeader(CollectionUtils.isEmpty(teacherEntity) ? 1L : teacherEntity.get(0).getId());
            entity.setCreatedAt(new Date());
            entity.setUpdatedAt(new Date());
            entities.add(entity);
        }

        entities = groupTeacherRepository.saveAll(entities);

        List<GroupTeacherMappingEntity> mappingEntities = new ArrayList<>();
        for (GroupTeacherEntity groupTeacherEntity : entities) {
            GroupTeacherMappingEntity newLeader = new GroupTeacherMappingEntity();
            newLeader.setTeacherId(groupTeacherEntity.getLeader());
            newLeader.setGroupId(groupTeacherEntity.getId());
            newLeader.setRole("leader");
            mappingEntities.add(newLeader);
        }
        groupTeacherMappingRepository.saveAll(mappingEntities);
    }

    @Async("async-thread-pool")
    public void uploadExcelGroupTeacherMapping(UploadGroupTeacherMappingRequest request) {
        for (GroupTeacherMappingUpload groupTeacherMapping : request.getGroupTeacherMappingCreateRequests()) {
            List<GroupTeacherEntity> groupTeacherEntities = groupTeacherRepository.findByName(groupTeacherMapping.getGroupName());
            Long groupId = CollectionUtils.isEmpty(groupTeacherEntities) ? 1L : groupTeacherEntities.get(0).getId();
            List<TeacherEntity> teacherEntities = teacherRepository.findByFullName(groupTeacherMapping.getTeacherName());
            Long teacherId = CollectionUtils.isEmpty(teacherEntities) ? 1L : teacherEntities.get(0).getId();

            List<GroupTeacherMappingEntity> entity = groupTeacherMappingRepository.findByGroupIdAndTeacherId(groupId, teacherId);
            if (CollectionUtils.isEmpty(entity)) {
                GroupTeacherMappingEntity groupTeacherMappingEntity = new GroupTeacherMappingEntity();
                groupTeacherMappingEntity.setGroupId(groupId);
                groupTeacherMappingEntity.setTeacherId(teacherId);
                groupTeacherMappingEntity.setRole(groupTeacherMapping.getRole());
                groupTeacherMappingRepository.save(groupTeacherMappingEntity);
            }
        }
    }
}
