package com.example.platform.service;

import com.example.platform.entity.Organization;
import java.util.List;

public interface OrganizationService {

    List<Organization> findAll();
    Organization findById(Long id);
    void create(Organization organization);
    void update(Organization organization);
    void delete(Long id);
    List<Organization> findByParentId(Long parentId);
}
