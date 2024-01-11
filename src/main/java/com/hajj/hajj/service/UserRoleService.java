package com.hajj.hajj.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.repository.UserRoleRepo;

@Service
public class UserRoleService {
    @Autowired
    UserRoleRepo userRoleRepo;

    public List<UserRole> getAllUserRole(){
        return userRoleRepo.findAll();
    }

    public Optional<UserRole> getUserRoleById(Long id){
        return userRoleRepo.findById(id);
    }

    public UserRole saveUserRole(UserRole userRole){
        return userRoleRepo.save(userRole);
    }

    public Optional<UserRole> updateUserRole(Long id,UserRole userRole){
        if(!userRoleRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Role With ID of "+id.toString()+" Not Found");
        }
        userRole.setId(id);
        return Optional.of(userRoleRepo.save(userRole));
    }
}
