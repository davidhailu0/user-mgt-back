package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.DTO.RoleHasPermissionRequest;
import com.hajj.hajj.model.Permission;
import com.hajj.hajj.model.Role;
import com.hajj.hajj.model.RoleHasPermission;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.PermissionRepo;
import com.hajj.hajj.repository.RoleHasPermissionRepo;
import com.hajj.hajj.repository.RoleRepo;
import com.hajj.hajj.repository.UsersRepo;

@Service
public class RoleHasPermissionService {
    @Autowired
    RoleHasPermissionRepo roleHasPermissionRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PermissionRepo permissionRepo;

    @Autowired
    UsersRepo usersRepo;

    public List<RoleHasPermission> getAllRolesWithPermissions(){
        return roleHasPermissionRepo.findAll();
    }

    public Optional<RoleHasPermission> getRoleHasPermissionById(Long id){
        return roleHasPermissionRepo.findById(id);
    }

    public RoleHasPermission saveRoleHasPermission(RoleHasPermissionRequest roleHasPermission){
        RoleHasPermission newRoleHasPermission = new RoleHasPermission();
        Optional<Role> assignedRole = roleRepo.findById(roleHasPermission.getRole());
        if(assignedRole.isPresent()){
            newRoleHasPermission.setRole(assignedRole.get());
        }
        newRoleHasPermission.setPermission((Permission[]) permissionRepo.findAllById(Arrays.asList(roleHasPermission.getPermission())).toArray());
        newRoleHasPermission.setStatus(roleHasPermission.getStatus());
        Optional<Users> created_by = usersRepo.findById(roleHasPermission.getCreated_by());
        if(created_by.isPresent()){
            newRoleHasPermission.setCreated_by(created_by.get());
            newRoleHasPermission.setUpdated_by(created_by.get());
        }
        LocalDateTime now = LocalDateTime.now();
        newRoleHasPermission.setCreated_at(Timestamp.valueOf(now));
        newRoleHasPermission.setUpdated_at(Timestamp.valueOf(now));
        return roleHasPermissionRepo.save(newRoleHasPermission);
    }

    public Optional<RoleHasPermission> updateRoleWithPermission(Long id, RoleHasPermissionRequest roleHasPermission) {
        if(!roleHasPermissionRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"RoleHasPermission with ID of "+id.toString()+" Not Found");
        }
        RoleHasPermission updatedRoleHasPermission = roleHasPermissionRepo.findById(id).get();
        Optional<Role> assignedRole = roleRepo.findById(roleHasPermission.getRole());
        if(assignedRole.isPresent()){
            updatedRoleHasPermission.setRole(assignedRole.get());
        }
        updatedRoleHasPermission.setPermission((Permission[]) permissionRepo.findAllById(Arrays.asList(roleHasPermission.getPermission())).toArray());
        updatedRoleHasPermission.setStatus(roleHasPermission.getStatus());
        Optional<Users> updated_by = usersRepo.findById(roleHasPermission.getUpdated_by());
        if(updated_by.isPresent()){
            updatedRoleHasPermission.setUpdated_by(updated_by.get());
        }
        updatedRoleHasPermission.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        return Optional.of(roleHasPermissionRepo.save(updatedRoleHasPermission));
    }
}
