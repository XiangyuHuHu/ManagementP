package com.example.platform.service;

import com.example.platform.entity.User;
import java.util.List;

public interface UserService {

    List<User> findAll();
    User findById(Long id);
    User findByUsername(String username);
    void create(User user);
    void update(User user);
    void delete(Long id);
    List<User> findByOrganizationId(Long organizationId);
}
