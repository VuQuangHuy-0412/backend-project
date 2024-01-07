package com.example.backendproject.service.sc5.helper;

import com.example.backendproject.entity.sc5.GroupTeacherEntity;
import com.example.backendproject.entity.sc5.SubjectEntity;
import com.example.backendproject.mapper.SubjectMapper;
import com.example.backendproject.model.sc5.SubjectUpload;
import com.example.backendproject.model.sc5.UploadSubjectRequest;
import com.example.backendproject.repository.sc5.GroupTeacherRepository;
import com.example.backendproject.repository.sc5.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SubjectServiceHelper {
    private final SubjectMapper subjectMapper;
    private final SubjectRepository subjectRepository;
    private final GroupTeacherRepository groupTeacherRepository;

    public SubjectServiceHelper(SubjectMapper subjectMapper,
                                SubjectRepository subjectRepository,
                                GroupTeacherRepository groupTeacherRepository) {
        this.subjectMapper = subjectMapper;
        this.subjectRepository = subjectRepository;
        this.groupTeacherRepository = groupTeacherRepository;
    }

    @Async("async-thread-pool")
    public void uploadFileSubject(UploadSubjectRequest request) {
        for (SubjectUpload subject : request.getSubjectCreateRequests()) {
            SubjectEntity entity = new SubjectEntity();
            entity.setName(subject.getName());
            entity.setCode(subject.getCode());
            List<GroupTeacherEntity> groupTeacherEntities = groupTeacherRepository.findByName(subject.getName());
            entity.setGroupId(CollectionUtils.isEmpty(groupTeacherEntities) ? 1L : groupTeacherEntities.get(0).getId());
            entity.setCreatedAt(new Date());
            entity.setUpdatedAt(new Date());
            subjectRepository.save(entity);
        }
    }
}
