package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.GroupTeacherMappingEntity;
import com.example.backendproject.mapper.GroupTeacherMapper;
import com.example.backendproject.mapper.GroupTeacherMappingMapper;
import com.example.backendproject.model.sc5.GroupTeacherMapping;
import com.example.backendproject.model.sc5.UploadGroupTeacherMappingRequest;
import com.example.backendproject.model.sc5.UploadGroupTeacherRequest;
import com.example.backendproject.repository.sc5.GroupTeacherMappingRepository;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public GroupTeacherServiceHelper(GroupTeacherMapper groupTeacherMapper,
                                     GroupTeacherRepository groupTeacherRepository,
                                     GroupTeacherMappingRepository groupTeacherMappingRepository,
                                     GroupTeacherMappingMapper groupTeacherMappingMapper) {
        this.groupTeacherMapper = groupTeacherMapper;
        this.groupTeacherRepository = groupTeacherRepository;
        this.groupTeacherMappingRepository = groupTeacherMappingRepository;
        this.groupTeacherMappingMapper = groupTeacherMappingMapper;
    }

    @Async("async-thread-pool")
    public void uploadFileGroupTeacher(UploadGroupTeacherRequest request) {
        List<GroupTeacherEntity> entities = groupTeacherMapper.toEntities(request.getGroupTeacherCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        entities = groupTeacherRepository.saveAll(entities);

        List<GroupTeacherMappingEntity> mappingEntities = new ArrayList<>();
        for (GroupTeacherEntity teacher : entities) {
            GroupTeacherMappingEntity newLeader = new GroupTeacherMappingEntity();
            newLeader.setTeacherId(teacher.getLeader());
            newLeader.setGroupId(teacher.getId());
            newLeader.setRole("leader");
            mappingEntities.add(newLeader);
        }
        groupTeacherMappingRepository.saveAll(mappingEntities);
    }

    @Async("async-thread-pool")
    public void uploadExcelGroupTeacherMapping(UploadGroupTeacherMappingRequest request) {
        List<GroupTeacherMapping> requests = request.getGroupTeacherMappingCreateRequests();
        for (GroupTeacherMapping groupTeacherMapping : request.getGroupTeacherMappingCreateRequests()) {
            GroupTeacherMappingEntity entity = groupTeacherMappingRepository.findByGroupIdAndTeacherId(groupTeacherMapping.getGroupId(), groupTeacherMapping.getTeacherId());
            if (entity != null) {
                List<GroupTeacherMapping> filter = requests.stream().filter(x -> Objects.equals(x.getGroupId(), groupTeacherMapping.getGroupId()) && Objects.equals(x.getTeacherId(), groupTeacherMapping.getTeacherId())).toList();
                requests.removeAll(filter);
            }
        }

        List<GroupTeacherMappingEntity> entities = groupTeacherMappingMapper.toEntities(requests);
        groupTeacherMappingRepository.saveAll(entities);
    }
}
