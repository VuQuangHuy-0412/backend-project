package com.example.backendproject.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;

public class LogUtil {
    public static void generateCorrelationId() {
        String correlationId = RandomStringUtils.randomAlphanumeric(20);
        MDC.put("correlationId", correlationId);
    }

    public static String getCorrelationId() {
        return MDC.get("traceId");
    }
}
