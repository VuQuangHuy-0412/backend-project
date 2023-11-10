package com.example.backendproject.config.exception;


import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.model.exception.ErrorResponse;
import com.example.backendproject.model.exception.RestErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(Sc5Exception.class)
    public ResponseEntity<ErrorResponse> handleSc5Exception(Sc5Exception sc5Exception) {
        if (sc5Exception.isInternal()) {
            log.error("SC5 Exception", sc5Exception);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("isSc5ErrorResponse", "true");
        headers.set("Content-Type", "application/json; charset=utf-8");
        return ResponseEntity.status(sc5Exception.getHttpStatus())
                .headers(headers)
                .body(sc5Exception.convertToErrorResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        Sc5Exception sc5Exception = new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        sc5Exception.initCause(ex);
        return handleSc5Exception(sc5Exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String message = "";
        if (!CollectionUtils.isEmpty(ex.getBindingResult().getAllErrors())) {
            message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        Sc5Exception sc5Exception = new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, message);
        return handleSc5Exception(sc5Exception);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public @ResponseBody
    ResponseEntity<ErrorResponse> handleNotSupportedMethodException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Sc5Exception sc5Exception = new Sc5Exception(ErrorEnum.NOT_SUPPORTED_METHOD, null);
        return handleSc5Exception(sc5Exception);
    }

    @ExceptionHandler(RestErrorException.class)
    public ResponseEntity<ErrorResponse> handleRestErrorException(RestErrorException ex) {
        Sc5Exception sc5Exception = new Sc5Exception(ErrorEnum.INTERNAL_SERVER_ERROR);
        String body = ex.getBody();
        if (StringUtils.isBlank(body)) {
            return handleSc5Exception(sc5Exception);
        }
        return handleSc5Exception(sc5Exception);
    }
}
