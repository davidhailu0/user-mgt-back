package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UsersRepo;
import com.hajj.hajj.service.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.RoleRequest;
import com.hajj.hajj.model.Role;
import com.hajj.hajj.service.RoleService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    @Autowired
    JWTUtil util;

    @Autowired
    UsersRepo usersRepo;
    @Autowired
    LoggerService loggerService;

    @GetMapping
    public List<Role> getAllRoles(HttpServletRequest request){
        Users user = getUser(request);
        List<Role> allRoles = roleService.getAllRoles();
//        loggerService.createNewLog(user,request.getRequestURI(),gson.toJson(allRoles));
        return allRoles;
    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id,HttpServletRequest request){
        Users user = getUser(request);
        Role role = roleService.getRoleById(id).get();
//        loggerService.createNewLog(user,request.getRequestURI(),gson.toJson(role));
        return role;
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Role registerNewRole(@Valid @RequestBody RoleRequest role,HttpServletRequest request){
        Users user = getUser(request);
        Role newRole = roleService.saveRole(role);
//        loggerService.createNewLog(user,request.getRequestURI(),gson.toJson(newRole));
        return newRole;
    }

    @PutMapping("/{id}")
    public Role updateRole(@RequestBody RoleRequest role,@PathVariable Long id,HttpServletRequest request){
        Users user = getUser(request);
        Role updatedRole = roleService.updateRole(id,role).orElse(null);
//        loggerService.createNewLog(user,request.getRequestURI(),gson.toJson(updatedRole));
        return updatedRole;
    }

    Users getUser(HttpServletRequest request){
        String jwtToken = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        return usersRepo.findUsersByUsername(username).get();
    }
}
