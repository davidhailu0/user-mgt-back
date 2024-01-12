package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.RoleHasPermissionRequest;
import com.hajj.hajj.model.RoleHasPermission;
import com.hajj.hajj.service.RoleHasPermissionService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/rolesHasPermission")
public class RoleHasPermissionController {
    @Autowired
    RoleHasPermissionService roleHasPermissionService;

    @GetMapping
    public List<RoleHasPermission> getAllRolesWithPermissions(){
        return roleHasPermissionService.getAllRolesWithPermissions();
    }

    @GetMapping("/{id}")
    public Optional<RoleHasPermission> getRolesWithPermissionById(@PathVariable Long id){
        return roleHasPermissionService.getRoleHasPermissionById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoleHasPermission registerRoleWithPermission(@Valid @RequestBody RoleHasPermissionRequest roleHasPermission){
        return roleHasPermissionService.saveRoleHasPermission(roleHasPermission);
    }

    @PutMapping("/{id}")
    public Optional<RoleHasPermission> updateRoleWithPermission(@RequestBody RoleHasPermissionRequest roleHasPermission,@PathVariable Long id){
        return roleHasPermissionService.updateRoleWithPermission(id,roleHasPermission);
    }
}
