package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.model.Role;
import com.hajj.hajj.service.RoleService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<Role> getAllRoles(){
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<Role> getRoleById(@PathVariable Long id){
        return roleService.getRoleById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Role registerNewRole(@Valid @RequestBody Role role){
        return roleService.saveRole(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<Role> updateRole(@RequestBody Role role,@PathVariable Long id){
        return roleService.updateRole(id,role);
    }
}
