package com.example.platform.mapper;

import com.example.platform.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrganizationMapper {

    List<Organization> findAll();
    Organization findById(Long id);
    void insert(Organization organization);
    void update(Organization organization);
    void delete(Long id);
    List<Organization> findByParentId(Long parentId);
}
