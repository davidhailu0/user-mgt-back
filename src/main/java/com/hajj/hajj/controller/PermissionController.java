package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.PermissionRequest;
import com.hajj.hajj.model.Permission;
import com.hajj.hajj.service.PermissionService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {
    @Autowired
    PermissionService permissionService;

    @GetMapping
    public List<Permission> getAllPermissions(){
        return permissionService.getAllPermission();
    }

    @GetMapping("/{id}")
    public Optional<Permission> getPermissionById(@PathVariable Long id){
        return permissionService.getPermissionById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Permission registerNewPermission(@Valid @RequestBody PermissionRequest permission){
        return permissionService.savePermission(permission);
    }

    @PutMapping("/{id}")
    public Optional<Permission> updatePermission(@PathVariable Long id,@RequestBody PermissionRequest permission){
        return permissionService.updatePermission(id,permission);
    }
}
