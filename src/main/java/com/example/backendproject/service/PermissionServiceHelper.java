package com.example.backendproject.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class PermissionServiceHelper {
    private static Map<String, String> permissions = new LinkedHashMap<>();
    private static Map<String, Method> uriMethodMap = new HashMap<>();

    public static Map<String, String> getPermissions() {
        return permissions;
    }

    public static Map<String, Method> getUriMethodMap() {
        return uriMethodMap;
    }
}
