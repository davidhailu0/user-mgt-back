package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<RoleHasPermission> getAllRolesWithPermissions(){
        return roleHasPermissionService.getAllRolesWithPermissions();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<RoleHasPermission> getRolesWithPermissionById(@PathVariable Long id){
        return roleHasPermissionService.getRoleHasPermissionById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RoleHasPermission registerRoleWithPermission(@Valid @RequestBody RoleHasPermission roleHasPermission){
        return roleHasPermissionService.saveRoleHasPermission(roleHasPermission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<RoleHasPermission> updateRoleWithPermission(@RequestBody RoleHasPermission roleHasPermission,@PathVariable Long id){
        return roleHasPermissionService.updateRoleWithPermission(id,roleHasPermission);
    }
}
