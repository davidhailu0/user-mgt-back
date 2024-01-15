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

import com.hajj.hajj.DTO.PermissionRequest;
import com.hajj.hajj.model.Permission;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.PermissionRepo;
import com.hajj.hajj.repository.UsersRepo;

@Service
public class PermissionService {
    @Autowired
    PermissionRepo permissionRepo;

    @Autowired
    UsersRepo usersRepo;

    // @PostConstruct
    // void addPermissions(){
    //     permissionRepo.save(new Permission("New",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Copy",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Delete",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Close",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Unlock",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Reopen",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Print",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Auth",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Reverse",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Rollover Components",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Confirm",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Liquidate",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Hold",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Template",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("View",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     permissionRepo.save(new Permission("Generate",null,null, Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    // }

    public List<Permission> getAllPermission(){
        return permissionRepo.findAll();
    }

    public Optional<Permission> getPermissionById(Long id){
        return Optional.ofNullable(permissionRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not Found")));
    }

    public Permission savePermission(PermissionRequest permissionInfo){
        Permission newPermission = new Permission();
        newPermission.setName(permissionInfo.getName());
        newPermission.setStatus(permissionInfo.getStatus());
        Optional<Users> created_by = usersRepo.findById(permissionInfo.getCreated_by());
        if(created_by.isPresent()){
            newPermission.setCreated_by(created_by.get());
            newPermission.setUpdated_by(created_by.get());
        }
        LocalDateTime now = LocalDateTime.now();
        newPermission.setCreated_at(Timestamp.valueOf(now));
        newPermission.setUpdated_at(Timestamp.valueOf(now));
        return permissionRepo.save(newPermission);
    }

    public Optional<Permission> updatePermission(Long id,PermissionRequest updatedPermission){
        if(!permissionRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Permission with ID of "+id.toString()+" not found");
        }
        Permission updatePermission = permissionRepo.findById(id).get();
        updatePermission.setName(updatedPermission.getName());
        updatePermission.setStatus(updatedPermission.getStatus());
        updatePermission.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        Optional<Users> updated_by = usersRepo.findById(updatedPermission.getUpdated_by());
        updated_by.ifPresent(updatePermission::setUpdated_by);
        return Optional.of(permissionRepo.save(updatePermission));
    }
}
