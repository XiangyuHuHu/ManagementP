package com.example.platform.service.impl;

import com.example.platform.entity.Organization;
import com.example.platform.mapper.OrganizationMapper;
import com.example.platform.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public List<Organization> findAll() {
        return organizationMapper.findAll();
    }

    @Override
    public Organization findById(Long id) {
        return organizationMapper.findById(id);
    }

    @Override
    public void create(Organization organization) {
        organizationMapper.insert(organization);
    }

    @Override
    public void update(Organization organization) {
        organizationMapper.update(organization);
    }

    @Override
    public void delete(Long id) {
        organizationMapper.delete(id);
    }

    @Override
    public List<Organization> findByParentId(Long parentId) {
        return organizationMapper.findByParentId(parentId);
    }
}
