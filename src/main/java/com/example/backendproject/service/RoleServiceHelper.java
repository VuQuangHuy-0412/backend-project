package com.example.backendproject.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceHelper {


    public Set<String> getPermissionForAdmin() {
        Set<String> result = new HashSet<>();
        PermissionServiceHelper.getPermissions().forEach((key, value) -> result.add(key));
        return result;
    }
}
