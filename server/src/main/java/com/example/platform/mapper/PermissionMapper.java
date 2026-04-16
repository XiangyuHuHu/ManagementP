package com.example.platform.mapper;

import com.example.platform.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PermissionMapper {

    List<Permission> findAll();
    Permission findById(Long id);
    void insert(Permission permission);
    void update(Permission permission);
    void delete(Long id);
    List<Permission> findByRoleId(Long roleId);
    List<Permission> findByParentId(Long parentId);
}
