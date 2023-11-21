package com.example.backendproject.service.sc5;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.entity.sc5.TeacherEntity;
import com.example.backendproject.mapper.TeacherMapper;
import com.example.backendproject.model.sc5.Teacher;
import com.example.backendproject.model.sc5.TeacherSearchRequest;
import com.example.backendproject.model.sc5.TeacherSearchResponse;
import com.example.backendproject.repository.sc5.TeacherRepository;
import com.example.backendproject.service.AdminLogService;
import com.example.backendproject.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final AdminLogService adminLogService;
    private final TeacherMapper teacherMapper;

    public TeacherService(TeacherRepository teacherRepository,
                          AdminLogService adminLogService,
                          TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.adminLogService = adminLogService;
        this.teacherMapper = teacherMapper;
    }

    public TeacherSearchResponse searchTeacher(TeacherSearchRequest request) {
        TeacherSearchResponse response = new TeacherSearchResponse();
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

        List<Teacher> data = teacherRepository.searchTeacherByFilter(request);
        response.setData(data);
        return response;
    }

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
        teacherEntity.setUpdatedAt(new Date());

        try {
            teacherRepository.save(teacherEntity);
        } catch (Exception exception) {
            log.error("Update teacher error!", exception);
            throw new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
