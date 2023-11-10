package com.example.backendproject.util;

import com.example.backendproject.model.AccessTokenPayload;
import org.slf4j.MDC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdminCommonUtil {

    public static AccessTokenPayload getCurrentAdminAccessTokenPayload() {
        String adminAccessToken = MDC.get("adminAccessToken");
        AccessTokenPayload payload = new AccessTokenPayload();
        if (adminAccessToken != null) {
            payload = CommonUtil.fromJson(adminAccessToken, AccessTokenPayload.class);
        } else {
            payload.setUsername("system");
        }
        return payload;
    }

    public static Long getCurrentUserId() {
        AccessTokenPayload accessTokenPayload = getCurrentAdminAccessTokenPayload();
        if (accessTokenPayload != null) {
            return accessTokenPayload.getUserId();
        }
        return null;
    }

    public static String getCurrentUserName() {
        AccessTokenPayload accessTokenPayload = getCurrentAdminAccessTokenPayload();
        if (accessTokenPayload != null) {
            return accessTokenPayload.getUsername();
        }
        return null;
    }

    public static boolean isValidRegex(String pwd, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }
}
