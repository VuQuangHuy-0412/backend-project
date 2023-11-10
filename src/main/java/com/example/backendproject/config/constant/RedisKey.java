package com.example.backendproject.config.constant;

public class RedisKey {
    public static final String GHTK_IAM_ACCESS_TOKEN = "ewallet_ghtk_iam_access_token";
    public static final String OTP_SENT_STATUS_PREFIX = "ewallet_otp_sent_status_";
    public static final String OTP_PREFIX = "ewallet_otp_";
    public static final String ACCESS_TOKENS_PREFIX = "ewallet_access_tokens_";
    public static final String ADMIN_ACCESS_TOKENS_PREFIX = "ewallet_admin_access_tokens_";
    public static final String REFRESH_TOKEN_PREFIX = "ewallet_refresh_token_";
    public static final String ADMIN_REFRESH_TOKEN_PREFIX = "ewallet_admin_refresh_token_";
    public static final String SESSION_PREFIX = "ewallet_session_";
    public static final String GHTK_ERP_ACCESS_TOKEN = "ewallet_ghtk_erp_access_token";

    public static final String VERIFY_PIN_COUNT = "ewallet_verify_pin_count_";
    public static final String VERIFY_PIN_BLOCK = "ewallet_verify_pin_block_";
    public static final String CACHE_BANK = "ewallet_financial_cache_bank_";
    public static final String CACHE_BANK_PAYMENT = "ewallet_financial_cache_bank_payment_";
    public static final String VPBANK_APP_TOKEN = "vpbank_app_token";

    public static final String NOTIFICATION_BALANCE_PREFIX = "ewallet-finance:notification-balance:";

    public static final String RETRY_TRANSFER_BANK_PREFIX = "retry_transfer_bank_";
    public static final String EXPORT_EXCEL_CHECK_STATUS = "ewallet_export_excel_check_status_";
    public static final String EXPORT_EXCEL_ACCOUNT_ERROR_CHECK_STATUS = "ewallet_export_excel_account_error_check_status_";
}
