package com.example.backendproject.config.constant;

public enum ErrorEnum {
    API_NOT_FOUND(404, "api_not_found", "api is not found"),
    INTERNAL_SERVER_ERROR(500, "internal_server_error", "Đã có lỗi xảy ra. Xin vui lòng thử lại sau hoặc liên hệ quản trị viên để được hỗ trợ."),
    ACCESS_DENIED(403, "access_denied", "Bạn không có quyền truy cập tài nguyên này"),
    INVALID_INPUT(400, "invalid_input", "Dữ liệu đầu vào không hợp lệ"),
    INPUT_MISSING(400, "invalid_input", "Vui lòng nhập đủ thông tin"),
    INVALID_INPUT_COMMON(400, "invalid_input", "%s"),
    INVALID_MOBILE_FORMAT(400, "invalid_mobile_format", "Số điện thoại không hợp lệ"),
    SYSTEM_ERROR(400, "system_error", "Lỗi hệ thống"),
    NOT_SUPPORTED_METHOD(405, "method_not_supported", "Không hỗ trợ phương thức này"),
    INVALID_ACCESS_TOKEN(401, "invalid_access_token", "Token không hợp lệ"),
    SESSION_EXPIRED(401, "session_expired", "Phiên đăng nhập đã hết, vui lòng đăng nhập lại"),
    ADMIN_INVALID_CREDENTIALS(400, "invalid_credentials", "Sai tên đăng nhập hoặc mật khẩu. Tài khoản sẽ bị khóa nếu bạn nhập sai mật khẩu quá %s lần"),
    OTP_AlREADY_CONFIRM(400, "invalid_otp", "Mã OTP đã hết hạn hoặc xác nhận trước đó"),
    USER_IS_LOCKED(400, "user_is_locked", "Tài khoản đã bị khóa"),
    INVALID_PASSWORD_FORMAT(400, "invalid_password_format", "Mật khẩu không hợp lệ"),
    INVALID_AMOUNT(400, "invalid_amount", "Số tiền không hợp lệ"),
    ;

    private Integer httpStatus;
    private String code;
    private String message;

    ErrorEnum(Integer httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
