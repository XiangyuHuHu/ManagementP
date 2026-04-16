package com.example.platform.service.impl;

import com.example.platform.entity.Permission;
import com.example.platform.mapper.PermissionMapper;
import com.example.platform.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAll() {
        return permissionMapper.findAll();
    }

    @Override
    public Permission findById(Long id) {
        return permissionMapper.findById(id);
    }

    @Override
    public void create(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.update(permission);
    }

    @Override
    public void delete(Long id) {
        permissionMapper.delete(id);
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        return permissionMapper.findByRoleId(roleId);
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        return permissionMapper.findByParentId(parentId);
    }
}
