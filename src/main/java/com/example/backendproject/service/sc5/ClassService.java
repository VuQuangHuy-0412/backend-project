package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.ClassEntity;
import com.example.backendproject.mapper.ClassMapper;
import com.example.backendproject.model.sc5.Class;
import com.example.backendproject.model.sc5.ClassSearchRequest;
import com.example.backendproject.model.sc5.ClassSearchResponse;
import com.example.backendproject.model.sc5.UploadClassRequest;
import com.example.backendproject.repository.sc5.ClassRepository;
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
public class ClassService {
    private final ClassRepository classRepository;
    private final AdminLogService adminLogService;
    private final ClassMapper classMapper;

    public ClassService(ClassRepository classRepository,
                        AdminLogService adminLogService,
                        ClassMapper classMapper) {
        this.classRepository = classRepository;
        this.adminLogService = adminLogService;
        this.classMapper = classMapper;
    }

    public ClassSearchResponse searchClass(ClassSearchRequest request) {
        ClassSearchResponse response = new ClassSearchResponse();
        response.setPage(request.getPage() + 1);
        response.setPageSize(request.getPageSize());

        List<Class> data = classRepository.searchClassByFilter(request);
        response.setData(data);
        return response;
    }

    public void createClass(Class classDto) {
        adminLogService.log("createClass", CommonUtil.toJson(classDto));
        log.info("Create class with data: " + CommonUtil.toJson(classDto));

        validateCreateClassRequest(classDto);
        ClassEntity classEntity = classMapper.toEntity(classDto);
        classEntity.setCreatedAt(new Date());
        classEntity.setUpdatedAt(new Date());
        try {
            classRepository.save(classEntity);
        } catch (Exception exception) {
            log.error("Save class error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCreateClassRequest(Class classDto) {
        if (StringUtils.isBlank(classDto.getName()) || StringUtils.isBlank(classDto.getCode())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thiếu thông tin tên hoặc mã lớp học");
        }
    }

    public void updateClass(Class classDto) {
        if (classDto.getId() == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }
        validateCreateClassRequest(classDto);

        Optional<ClassEntity> classEntityOptional = classRepository.findById(classDto.getId());
        if (classEntityOptional.isEmpty()) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Không tìm thấy giảng viên.");
        }

        ClassEntity classEntity = classEntityOptional.get();
        classEntity.setName(classDto.getName());
        classEntity.setCode(classDto.getCode());
        classEntity.setSemester(classDto.getSemester());
        classEntity.setSubjectId(classDto.getSubjectId());
        classEntity.setWeek(classDto.getWeek());
        classEntity.setDayOfWeek(classDto.getDayOfWeek());
        classEntity.setTimeOfDay(classDto.getTimeOfDay());
        classEntity.setTimeOfClass(classDto.getTimeOfClass());
        classEntity.setLanguageId(classDto.getLanguageId());
        classEntity.setIsAssigned(classDto.getIsAssigned());
        classEntity.setTeacherId(classDto.getTeacherId());
        classEntity.setBuilding(classDto.getBuilding());
        classEntity.setRoom(classDto.getRoom());
        classEntity.setUpdatedAt(new Date());

        try {
            classRepository.save(classEntity);
        } catch (Exception exception) {
            log.error("Update class error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFileClass(UploadClassRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getClassCreateRequests())) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT);
        }

        for (Class classDto : request.getClassCreateRequests()) {
            validateCreateClassRequest(classDto);
        }

        List<ClassEntity> entities = classMapper.toEntities(request.getClassCreateRequests());
        entities.forEach(x -> x.setCreatedAt(new Date()));
        entities.forEach(x -> x.setUpdatedAt(new Date()));

        classRepository.saveAll(entities);
    }
}
