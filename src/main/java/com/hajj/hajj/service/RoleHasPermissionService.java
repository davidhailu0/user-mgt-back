package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
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

//    @PostConstruct
//    void addPermissionForRoles(){
//        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
//        roleHasPermissionRepo.save(new RoleHasPermission(roleRepo.findById(1L).get(), List.of(permissionRepo.findById(2L).get()),null,null,time,time,"Active"));
//        roleHasPermissionRepo.save(new RoleHasPermission(roleRepo.findById(2L).get(), List.of(permissionRepo.findById(1L).get()),null,null,time,time,"Active"));
//    }

    public List<RoleHasPermission> getAllRolesWithPermissions(){
        return roleHasPermissionRepo.findAll();
    }

    public Optional<RoleHasPermission> getRoleHasPermissionById(Long id){
        return Optional.ofNullable(roleHasPermissionRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not Found")));
    }

    public RoleHasPermission saveRoleHasPermission(RoleHasPermissionRequest roleHasPermission){
        RoleHasPermission newRoleHasPermission = new RoleHasPermission();
        Optional<Role> assignedRole = roleRepo.findById(roleHasPermission.getRole());
        assignedRole.ifPresent(newRoleHasPermission::setRole);
        newRoleHasPermission.setPermissions(permissionRepo.findAllById(Arrays.asList(roleHasPermission.getPermission())));
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
        assignedRole.ifPresent(updatedRoleHasPermission::setRole);
        updatedRoleHasPermission.setPermissions(permissionRepo.findAllById(Arrays.asList(roleHasPermission.getPermission())));
        updatedRoleHasPermission.setStatus(roleHasPermission.getStatus());
        Optional<Users> updated_by = usersRepo.findById(roleHasPermission.getUpdated_by());
        updated_by.ifPresent(updatedRoleHasPermission::setUpdated_by);
        updatedRoleHasPermission.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        return Optional.of(roleHasPermissionRepo.save(updatedRoleHasPermission));
    }
}
