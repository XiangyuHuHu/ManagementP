package com.example.platform.controller;

import com.example.platform.entity.Organization;
import com.example.platform.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public List<Organization> findAll() {
        return organizationService.findAll();
    }

    @GetMapping("/{id}")
    public Organization findById(@PathVariable Long id) {
        return organizationService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody Organization organization) {
        organizationService.create(organization);
    }

    @PutMapping
    public void update(@RequestBody Organization organization) {
        organizationService.update(organization);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        organizationService.delete(id);
    }

    @GetMapping("/parent/{parentId}")
    public List<Organization> findByParentId(@PathVariable Long parentId) {
        return organizationService.findByParentId(parentId);
    }
}
