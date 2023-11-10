package com.example.backendproject.config.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private String body;
    private static final Set<String> WHITE_LIST_UPLOAD_PATH = Set.of();

    public XSSRequestWrapper(HttpServletRequest servletRequest) throws IOException {
        super(servletRequest);

        if (WHITE_LIST_UPLOAD_PATH.contains(servletRequest.getRequestURI())) {
            body = StringUtils.EMPTY;
        } else {
            body = IOUtils.toString(servletRequest.getReader());
        }
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);

        return stripXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    private String stripXSS(String value) {
        if (value != null) {

            // Avoid null characters
            value = value.replaceAll("\0", "");
            value = Jsoup.clean(value, Safelist.simpleText());
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        body = stripXSS(body);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

        };
    }

}