package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
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

//    @PostConstruct
//    void addRoleToUsers(){
//        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
//        //userRoleRepo.save(new UserRole(roleRepo.findById(1L).get(),usersRepo.findById(1L).get(),null,null,time,time,"Active"));
//        //userRoleRepo.save(new UserRole(roleRepo.findById(2L).get(),usersRepo.findById(2L).get(),null,null,time,time,"Active"));
//        //userRoleRepo.save(new UserRole(roleRepo.findById(19L).get(),usersRepo.findById(3L).get(),null,null,time,time,"Active"));
//    }

    public List<UserRole> getAllUserRole(){
        return userRoleRepo.findAll();
    }

    public Optional<UserRole> getUserRoleById(Long id){
        return Optional.ofNullable(userRoleRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Role not Found")));
    }

    public UserRole saveUserRole(UserRoleRequest userRole){
        UserRole newUserRole = new UserRole();
        Optional<Role> assignedRole = roleRepo.findById(userRole.getRole());
        assignedRole.ifPresent(newUserRole::setRole);
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
        user.ifPresent(newUserRole::setUser);
        return userRoleRepo.save(newUserRole);
    }

    public Optional<UserRole> updateUserRole(Long id,UserRoleRequest userRole){
        if(!userRoleRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Role With ID of "+id.toString()+" Not Found");
        }
        UserRole updatedUserRole = userRoleRepo.findById(id).get();
        Optional<Role> assignedRole = roleRepo.findById(userRole.getRole());
        assignedRole.ifPresent(updatedUserRole::setRole);
        Optional<Users> assignedByUser = usersRepo.findById(userRole.getUpdated_by());
        if(assignedRole.isPresent()){
            updatedUserRole.setUpdated_by(assignedByUser.get());
        }
        updatedUserRole.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        updatedUserRole.setStatus(userRole.getStatus());
        Optional<Users> user = usersRepo.findById(userRole.getUser());
        user.ifPresent(updatedUserRole::setUser);
        return Optional.of(userRoleRepo.save(updatedUserRole));
    }
}
