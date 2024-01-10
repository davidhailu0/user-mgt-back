package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.service.UserRoleService;

import jakarta.validation.Valid;

@CrossOrigin
@RequestMapping("/api/v1/userRole")
@RestController
public class UserRoleController {
    @Autowired
    UserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<UserRole> getAllUserRole(){
        return userRoleService.getAllUserRole();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<UserRole> getUserRoleById(@PathVariable Long id){
        return userRoleService.getUserRoleById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public UserRole registerUserRole(@Valid @RequestBody UserRole userRole){
        return userRoleService.saveUserRole(userRole);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<UserRole> updateUserRole(@RequestBody UserRole userRole,@PathVariable Long id){
        return userRoleService.updateUserRole(id,userRole);
    }
}
