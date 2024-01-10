package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.model.Permission;
import com.hajj.hajj.repository.PermissionRepo;

@Service
public class PermissionService {
    @Autowired
    PermissionRepo permissionRepo;

    @PostConstruct
    void addPermissions(){
        permissionRepo.save(new Permission("New",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Copy",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Delete",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Close",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Unlock",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Reopen",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Print",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Auth",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Reverse",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Rollover Components",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Confirm",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Liquidate",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Hold",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Template",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("View",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
        permissionRepo.save(new Permission("Generate",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    }

    public List<Permission> getAllPermission(){
        return permissionRepo.findAll();
    }

    public Optional<Permission> getPermissionById(Long id){
        return permissionRepo.findById(id);
    }

    public Permission savePermission(Permission permissionInfo){
        return permissionRepo.save(permissionInfo);
    }

    public Optional<Permission> updatePermission(Long id,Permission updatedPermission){
        if(!permissionRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Permission with ID of "+id.toString()+" not found");
        }
        return Optional.of(permissionRepo.save(updatedPermission));
    }
}
