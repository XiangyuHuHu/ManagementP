package com.example.platform.service;

import com.example.platform.entity.Permission;
import java.util.List;

public interface PermissionService {

    List<Permission> findAll();
    Permission findById(Long id);
    void create(Permission permission);
    void update(Permission permission);
    void delete(Long id);
    List<Permission> findByRoleId(Long roleId);
    List<Permission> findByParentId(Long parentId);
}
