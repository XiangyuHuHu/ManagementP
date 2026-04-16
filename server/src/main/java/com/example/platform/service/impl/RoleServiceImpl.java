package com.example.platform.service.impl;

import com.example.platform.entity.Role;
import com.example.platform.mapper.RoleMapper;
import com.example.platform.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Override
    public void create(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.update(role);
    }

    @Override
    public void delete(Long id) {
        roleMapper.delete(id);
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        return roleMapper.findByUserId(userId);
    }
}
