package com.example.backendproject.config.exception;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.model.exception.ErrorResponse;
import com.example.backendproject.util.LogUtil;
import lombok.Data;

@Data
public class Sc5Exception extends RuntimeException {
    private boolean isInternal; // Exception fired within current service or not
    private Integer httpStatus;
    private String errorCode;

    public Sc5Exception(ErrorEnum errorEnum, String... args) {
        super(String.format(errorEnum.getMessage(), args));
        this.isInternal = true;
        this.httpStatus = errorEnum.getHttpStatus();
        this.errorCode = errorEnum.getCode();
    }

    public Sc5Exception(int httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public Sc5Exception(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.isInternal = false;
        this.httpStatus = errorResponse.getHttpStatus();
        this.errorCode = errorResponse.getCode();
    }

    public Sc5Exception(String msg) {
        this(ErrorEnum.INVALID_INPUT_COMMON, msg);
    }

    public boolean isErrorEnum(ErrorEnum errorEnum) {
        return errorEnum.getCode().equals(this.errorCode);
    }

    public ErrorResponse convertToErrorResponse() {
        ErrorResponse ret = new ErrorResponse();
        ret.setHttpStatus(this.httpStatus);
        ret.setCode(this.errorCode);
        ret.setMessage(this.getMessage());
        ret.setTraceId(LogUtil.getCorrelationId());
        return ret;
    }
}
