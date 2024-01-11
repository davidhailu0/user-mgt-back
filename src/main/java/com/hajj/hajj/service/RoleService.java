package com.hajj.hajj.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.model.Role;
import com.hajj.hajj.repository.RoleRepo;

@Service
public class RoleService {
    @Autowired
    RoleRepo roleRepo;

    public List<Role> getAllRoles(){
        return roleRepo.findAll();
    }

    public Optional<Role> getRoleById(Long id){
        return roleRepo.findById(id);
    }

    public Role saveRole(Role role){
        return roleRepo.save(role);
    }

    public Optional<Role> updateRole(Long id,Role role){
        if(!roleRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role With ID of "+id.toString()+" Not Found");
        }
        role.setId(id);
        return Optional.of(roleRepo.save(role));
    }
}
