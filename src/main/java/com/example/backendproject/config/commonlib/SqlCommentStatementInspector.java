package com.example.backendproject.config.commonlib;

import com.example.backendproject.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.net.InetAddress;

@Slf4j
public class SqlCommentStatementInspector implements StatementInspector {
    private static String HOST_NAME;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {

        }
    }

    @Override
    public String inspect(String sql) {
        sql = sql + " /* " + HOST_NAME + " ; traceId: " + LogUtil.getCorrelationId() + " */" ;
        return sql;
    }
}