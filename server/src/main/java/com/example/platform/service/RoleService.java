package com.example.platform.service;

import com.example.platform.entity.Role;
import java.util.List;

public interface RoleService {

    List<Role> findAll();
    Role findById(Long id);
    void create(Role role);
    void update(Role role);
    void delete(Long id);
    List<Role> findByUserId(Long userId);
}
