package com.example.backendproject.config.filter;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class PrometheusAuthFilter extends OncePerRequestFilter {
    private final String tokenPrometheus;

    public PrometheusAuthFilter(String tokenPrometheus) {
        this.tokenPrometheus = tokenPrometheus;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI() != null && request.getRequestURI().contains("actuator/prometheus")){
            if (StringUtils.isBlank(request.getHeader("Authorization")) ||
                    !request.getHeader("Authorization").equals(tokenPrometheus)){
                throw new Sc5Exception(ErrorEnum.ACCESS_DENIED);
            }
        }
        filterChain.doFilter(request, response);
    }
}
