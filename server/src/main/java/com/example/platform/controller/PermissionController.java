package com.example.platform.controller;

import com.example.platform.entity.Permission;
import com.example.platform.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public List<Permission> findAll() {
        return permissionService.findAll();
    }

    @GetMapping("/{id}")
    public Permission findById(@PathVariable Long id) {
        return permissionService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody Permission permission) {
        permissionService.create(permission);
    }

    @PutMapping
    public void update(@RequestBody Permission permission) {
        permissionService.update(permission);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        permissionService.delete(id);
    }

    @GetMapping("/role/{roleId}")
    public List<Permission> findByRoleId(@PathVariable Long roleId) {
        return permissionService.findByRoleId(roleId);
    }

    @GetMapping("/parent/{parentId}")
    public List<Permission> findByParentId(@PathVariable Long parentId) {
        return permissionService.findByParentId(parentId);
    }
}
