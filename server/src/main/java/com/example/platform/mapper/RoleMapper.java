package com.example.platform.mapper;

import com.example.platform.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> findAll();
    Role findById(Long id);
    void insert(Role role);
    void update(Role role);
    void delete(Long id);
    List<Role> findByUserId(Long userId);
}
