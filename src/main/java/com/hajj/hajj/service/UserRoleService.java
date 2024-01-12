package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.DTO.UserRoleRequest;
import com.hajj.hajj.model.Role;
import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.RoleRepo;
import com.hajj.hajj.repository.UserRoleRepo;
import com.hajj.hajj.repository.UsersRepo;

@Service
public class UserRoleService {
    @Autowired
    UserRoleRepo userRoleRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UsersRepo usersRepo;

    public List<UserRole> getAllUserRole(){
        return userRoleRepo.findAll();
    }

    public Optional<UserRole> getUserRoleById(Long id){
        return userRoleRepo.findById(id);
    }

    public UserRole saveUserRole(UserRoleRequest userRole){
        UserRole newUserRole = new UserRole();
        Optional<Role> assignedRole = roleRepo.findById(userRole.getRole());
        if(assignedRole.isPresent()){
            newUserRole.setRole(assignedRole.get());
        }
        Optional<Users> assignedByUser = usersRepo.findById(userRole.getAssigned_by());
        if(assignedRole.isPresent()){
            newUserRole.setAssigned_by(assignedByUser.get());
            newUserRole.setUpdated_by(assignedByUser.get());
        }
        LocalDateTime now = LocalDateTime.now();
        newUserRole.setCreated_at(Timestamp.valueOf(now));
        newUserRole.setUpdated_at(Timestamp.valueOf(now));
        newUserRole.setStatus(userRole.getStatus());
        Optional<Users> user = usersRepo.findById(userRole.getUser());
        if(user.isPresent()){
            newUserRole.setUser(user.get());
        }
        return userRoleRepo.save(newUserRole);
    }

    public Optional<UserRole> updateUserRole(Long id,UserRoleRequest userRole){
        if(!userRoleRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Role With ID of "+id.toString()+" Not Found");
        }
        UserRole updatedUserRole = userRoleRepo.findById(id).get();
        Optional<Role> assignedRole = roleRepo.findById(userRole.getRole());
        if(assignedRole.isPresent()){
            updatedUserRole.setRole(assignedRole.get());
        }
        Optional<Users> assignedByUser = usersRepo.findById(userRole.getUpdated_by());
        if(assignedRole.isPresent()){
            updatedUserRole.setUpdated_by(assignedByUser.get());
        }
        updatedUserRole.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        updatedUserRole.setStatus(userRole.getStatus());
        Optional<Users> user = usersRepo.findById(userRole.getUser());
        if(user.isPresent()){
            updatedUserRole.setUser(user.get());
        }
        return Optional.of(userRoleRepo.save(updatedUserRole));
    }
}
