package com.example.platform.controller;

import com.example.platform.entity.Role;
import com.example.platform.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public Role findById(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody Role role) {
        roleService.create(role);
    }

    @PutMapping
    public void update(@RequestBody Role role) {
        roleService.update(role);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @GetMapping("/user/{userId}")
    public List<Role> findByUserId(@PathVariable Long userId) {
        return roleService.findByUserId(userId);
    }
}
