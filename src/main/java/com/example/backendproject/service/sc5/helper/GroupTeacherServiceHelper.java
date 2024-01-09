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
            try {
                GroupTeacherEntity entity = new GroupTeacherEntity();
                entity.setName(groupTeacherUpload.getName());
                entity.setDescription(groupTeacherUpload.getDescription());
                List<TeacherEntity> teacherEntity = teacherRepository.findByFullNameAndDataset(groupTeacherUpload.getLeaderName(), request.getDataset());
                if (!CollectionUtils.isEmpty(teacherEntity)) {
                    entity.setLeader(teacherEntity.get(0).getId());
                }
                entity.setDataset(request.getDataset());
                entity.setCreatedAt(new Date());
                entity.setUpdatedAt(new Date());
                entities.add(entity);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }

        entities = groupTeacherRepository.saveAll(entities);

        List<GroupTeacherMappingEntity> mappingEntities = new ArrayList<>();
        for (GroupTeacherEntity groupTeacherEntity : entities) {
            GroupTeacherMappingEntity newLeader = new GroupTeacherMappingEntity();
            newLeader.setTeacherId(groupTeacherEntity.getLeader());
            newLeader.setGroupId(groupTeacherEntity.getId());
            newLeader.setRole("leader");
            newLeader.setDataset(request.getDataset());
            mappingEntities.add(newLeader);
        }
        groupTeacherMappingRepository.saveAll(mappingEntities);
    }

    @Async("async-thread-pool")
    public void uploadExcelGroupTeacherMapping(UploadGroupTeacherMappingRequest request) {
        for (GroupTeacherMappingUpload groupTeacherMapping : request.getGroupTeacherMappingCreateRequests()) {
            try {
                List<GroupTeacherEntity> groupTeacherEntities = groupTeacherRepository.findByNameAndDataset(groupTeacherMapping.getGroupName(), request.getDataset());
                List<TeacherEntity> teacherEntities = teacherRepository.findByFullNameAndDataset(groupTeacherMapping.getTeacherName(), request.getDataset());
                if (!CollectionUtils.isEmpty(groupTeacherEntities) && !CollectionUtils.isEmpty(teacherEntities)) {
                    Long groupId = groupTeacherEntities.get(0).getId();
                    Long teacherId = teacherEntities.get(0).getId();

                    List<GroupTeacherMappingEntity> entity = groupTeacherMappingRepository.findByGroupIdAndTeacherIdAndDataset(groupId, teacherId, request.getDataset());
                    if (CollectionUtils.isEmpty(entity)) {
                        GroupTeacherMappingEntity groupTeacherMappingEntity = new GroupTeacherMappingEntity();
                        groupTeacherMappingEntity.setGroupId(groupId);
                        groupTeacherMappingEntity.setTeacherId(teacherId);
                        groupTeacherMappingEntity.setRole(groupTeacherMapping.getRole());
                        groupTeacherMappingEntity.setDataset(request.getDataset());
                        groupTeacherMappingRepository.save(groupTeacherMappingEntity);
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
