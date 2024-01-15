package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.DTO.UsersRequest;
import com.hajj.hajj.model.Branch;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.BranchRepo;
import com.hajj.hajj.repository.UserRoleRepo;
import com.hajj.hajj.repository.UsersRepo;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
    @Autowired
    UsersRepo userRepo;

    @Autowired
    UserRoleRepo userRoleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BranchRepo branchRepo;

    // @PostConstruct
    // void addUser(){
    //     userRepo.save(new Users("dawit","1234",passwordEncoder.encode("1234"),null,null,null,Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    //     userRepo.save(new Users("abdi","1234",passwordEncoder.encode("1234"),null,null,null,Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
    // }
    public List<Users> getAllUsers(){
        return userRepo.findAll();
    }

    public Optional<Users> getUserById(@NonNull Long id){
        return Optional.ofNullable(userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    public Users saveUser(@NonNull UsersRequest userInfo){
        Users newUser = new Users();
        Optional<Branch> userBranch = branchRepo.findById(userInfo.getBranch());
        if(userBranch.isPresent()){
            newUser.setBranch(userBranch.get());
        }
        newUser.setUsername(userInfo.getUsername());
        newUser.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        newUser.setSalt(userInfo.getSalt());
        newUser.setStatus(userInfo.getStatus());
        Optional<Users> created_by = userRepo.findById(userInfo.getCreated_by());
        if(created_by.isPresent()){
            newUser.setCreated_by(created_by.get());
            newUser.setUpdated_by(created_by.get());
        }
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreated_at(Timestamp.valueOf(now));
        newUser.setUpdated_at(Timestamp.valueOf(now));
        return userRepo.save(newUser);
    }

    public Optional<Users> updateUser(Long id,UsersRequest updatedUser){
        if(!userRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User With ID of "+id.toString()+" Not Found");
        }
        Users updateUser = userRepo.findById(id).get();
        if(!updateUser.getPassword().equals(updatedUser.getPassword())){
            updateUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        Optional<Branch> userBranch = branchRepo.findById(updatedUser.getBranch());
        if(userBranch.isPresent()){
            updateUser.setBranch(userBranch.get());
        }
        updateUser.setUsername(updatedUser.getUsername());
        updateUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        updateUser.setSalt(updatedUser.getSalt());
        updateUser.setStatus(updatedUser.getStatus());
        Optional<Users> updated_by = userRepo.findById(updatedUser.getUpdated_by());
        if(updated_by.isPresent()){
            updateUser.setUpdated_by(updated_by.get());
        }
        updateUser.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        return Optional.of(userRepo.save(updateUser));
    }
}
