package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.DTO.RoleRequest;
import com.hajj.hajj.model.Role;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.RoleRepo;
import com.hajj.hajj.repository.UsersRepo;

@Service
public class RoleService {
    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UsersRepo userRepo;

    public List<Role> getAllRoles(){
        return roleRepo.findAll();
    }

    public Optional<Role> getRoleById(Long id){
        return Optional.ofNullable(roleRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not Found")));
    }

    public Role saveRole(RoleRequest role){
        Role newRole = new Role();
        newRole.setName(role.getName());
        newRole.setDescription(role.getDescription());
        newRole.setStatus(role.getStatus());
        Optional<Users> created_by = userRepo.findById(role.getCreated_by());
        if(created_by.isPresent()){
            newRole.setCreated_by(created_by.get());
            newRole.setUpdated_by(created_by.get());
        }
        LocalDateTime now = LocalDateTime.now();
        newRole.setCreated_at(Timestamp.valueOf(now));
        newRole.setUpdated_at(Timestamp.valueOf(now));
        return roleRepo.save(newRole);
    }

    public Optional<Role> updateRole(Long id,RoleRequest role){
        if(!roleRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role With ID of "+id.toString()+" Not Found");
        }
        Role updatedRole = roleRepo.findById(id).get();
        updatedRole.setName(role.getName());
        updatedRole.setDescription(role.getDescription());
        updatedRole.setStatus(role.getStatus());
        updatedRole.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        Optional<Users> updated_by = userRepo.findById(role.getUpdated_by());
        if(updated_by.isPresent()){
            updatedRole.setUpdated_by(updated_by.get());
        }
        return Optional.of(roleRepo.save(updatedRole));
    }
}
