package com.example.backendproject.service;

import com.example.backendproject.util.ApiDescription;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.TargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;

@Service
@Slf4j
public class PermissionService {
    private final ApplicationContext context;

    public PermissionService(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    private void init() {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RestController.class);
        for (Map.Entry<String, Object> e : beansWithAnnotation.entrySet()) {
            Object bean = e.getValue();
            CglibHelper cglibHelper = new CglibHelper(bean);
            Object realObj = cglibHelper.getTargetObject();
            for (Method method : realObj.getClass().getMethods()) {
                String uri = getUri(method);
                if (uri == null) {
                    continue;
                }
                PermissionServiceHelper.getUriMethodMap().put(uri, method);
                if (e.getClass().isAnnotationPresent(ApiDescription.class)) {
                    if (!realObj.getClass().getAnnotation(ApiDescription.class).show()) {
                        continue;
                    }
                }
                if (method.isAnnotationPresent(ApiDescription.class)) {
                    ApiDescription apiDescription = method.getAnnotation(ApiDescription.class);
                    if (apiDescription.show()) {
                        PermissionServiceHelper.getPermissions().put(apiDescription.code(), apiDescription.value());
                    }
                }
            }
        }
    }

    private String getUri(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).value()[0];
        }

        if (method.isAnnotationPresent(PostMapping.class)) {
            return method.getAnnotation(PostMapping.class).value()[0];
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            return method.getAnnotation(PutMapping.class).value()[0];
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return method.getAnnotation(DeleteMapping.class).value()[0];
        }

        if (method.isAnnotationPresent(RequestMapping.class)) {
            return method.getAnnotation(RequestMapping.class).value()[0];
        }

        return null;
    }


    public Map<String, String> list() {
        return PermissionServiceHelper.getPermissions();
    }

    public static class CglibHelper {
        private final Object proxied;

        public CglibHelper(Object proxied) {
            this.proxied = proxied;
        }

        public Object getTargetObject() {
            String name = proxied.getClass().getName();
            if (name.toLowerCase().contains("cglib")) {
                return extractTargetObject(proxied);
            }
            return proxied;
        }

        private Object extractTargetObject(Object proxied) {
            try {
                return findSpringTargetSource(proxied).getTarget();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private TargetSource findSpringTargetSource(Object proxied) {
            Method[] methods = proxied.getClass().getDeclaredMethods();
            Method targetSourceMethod = findTargetSourceMethod(methods);
            targetSourceMethod.setAccessible(true);
            try {
                return (TargetSource) targetSourceMethod.invoke(proxied);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private Method findTargetSourceMethod(Method[] methods) {
            for (Method method : methods) {
                if (method.getName().endsWith("getTargetSource")) {
                    return method;
                }
            }
            throw new IllegalStateException(
                    "Could not find target source method on proxied object ["
                            + proxied.getClass() + "]");
        }
    }
}
