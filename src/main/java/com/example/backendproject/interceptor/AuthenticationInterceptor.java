package com.example.backendproject.interceptor;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.constant.RedisKey;
import com.example.backendproject.config.exception.Sc5Exception;
import com.example.backendproject.model.AccessTokenPayload;
import com.example.backendproject.service.CipherService;
import com.example.backendproject.service.PermissionServiceHelper;
import com.example.backendproject.service.RoleService;
import com.example.backendproject.util.AES;
import com.example.backendproject.util.ApiDescription;
import com.example.backendproject.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private static final List<String> PUBLIC_APIS = List.of("/login", "/auth/token/refresh");
    private static final List<String> AUTHENTICATED_APIS = List.of("/logout", "/change-password", "/profile");

    @Value("${ewallet.secretKey}")
    private String secretKey;

    @Value("${ewallet.secretIV}")
    private String secretIV;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CipherService cipherService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) { // allow preflight CORS request
            return true;
        }

        String path = request.getRequestURI();

        if (PUBLIC_APIS.contains(path)) {
            return true;
        }

        Method method = null;
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (Map.Entry<String, Method> entry : PermissionServiceHelper.getUriMethodMap().entrySet()) {
            if (antPathMatcher.match(entry.getKey(), path)) {
                method = entry.getValue();
                break;
            }
        }
        if (method == null) {
            throw new Sc5Exception(ErrorEnum.API_NOT_FOUND);
        }

        String accessToken = request.getHeader("Authorization");
        if (StringUtils.isBlank(accessToken)) {
            logger.error("Token is blank");
            throw new Sc5Exception(ErrorEnum.INVALID_ACCESS_TOKEN);
        }

        String tokenPayloadJson = AES.decrypt(accessToken, secretKey, secretIV);
        AccessTokenPayload tokenPayload = CommonUtil.fromJson(tokenPayloadJson, AccessTokenPayload.class);

        if (tokenPayload.getExpiredTime() == null || tokenPayload.getExpiredTime() < System.currentTimeMillis()) {
            logger.error("Token is expired");
            throw new Sc5Exception(ErrorEnum.SESSION_EXPIRED);
        }

        Set<String> members = redisTemplate.opsForSet().members(RedisKey.ADMIN_ACCESS_TOKENS_PREFIX + tokenPayload.getUserId());
        if (members == null) {
            throw new Sc5Exception(ErrorEnum.SESSION_EXPIRED);
        }
        if (members.stream().noneMatch(item -> cipherService.check(accessToken, item))) {
            throw new Sc5Exception(ErrorEnum.SESSION_EXPIRED);
        }

        MDC.put("adminAccessToken", tokenPayloadJson);
        if (AUTHENTICATED_APIS.contains(path)) {
            return true;
        }

        List<String> listRole = Arrays.stream(tokenPayload.getRoles().split(",")).map(String::trim).toList();
        if (!listRole.contains("admin")) {
            Set<String> permissions = roleService.getSetPermissionByRoles(tokenPayload.getRoles());
            if (method.isAnnotationPresent(ApiDescription.class)) {
                ApiDescription apiDescription = method.getAnnotation(ApiDescription.class);
                String code = apiDescription.code();
                if (StringUtils.isEmpty(code) || StringUtils.isNotEmpty(apiDescription.equal())) {
                    code = apiDescription.equal();
                }
                String finalCode = code.trim();
                boolean isPermitted = permissions.stream().anyMatch(e -> e.trim().equals(finalCode));
                if (!isPermitted) {
                    throw new Sc5Exception(ErrorEnum.ACCESS_DENIED);
                }
            } else {
                return true;
            }
        }
        return true;
    }
}
