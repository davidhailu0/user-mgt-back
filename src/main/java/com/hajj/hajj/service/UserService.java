package com.hajj.hajj.service;

import com.hajj.hajj.DTO.UserRoleRequest;
import com.hajj.hajj.DTO.UsersRequest;
import com.hajj.hajj.model.*;
import com.hajj.hajj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    @Autowired
    UsersRepo userRepo;

    @Autowired
    UserDetailRepo userDetailRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BranchRepo branchRepo;

    @Autowired
    UserRoleRepo userRoleRepo;

    @Autowired
    RoleRepo roleRepo;

//     @PostConstruct
//     void addUser(){
//         userRepo.save(new Users("fedila","1234",passwordEncoder.encode("1234"),null,null,null,Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()),"Active"));
//     }
    public List<Users> getAllUsers(){
        return userRepo.findAll();
    }

    public Optional<Users> getUserById(@NonNull Long id){
        if(!userRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userRepo.findById(id);
    }

    public Users saveUser(@NonNull UsersRequest userInfo,Users admin){
        Users newUser = new Users();
        Optional<Branch> userBranch = branchRepo.findById(userInfo.getBranch());
        userBranch.ifPresent(newUser::setBranch);
        newUser.setUsername(userInfo.getUsername());
        newUser.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        newUser.setSalt(userInfo.getSalt());
        newUser.setStatus(userInfo.getStatus());
        Optional<Users> created_by = userRepo.findById(userInfo.getCreated_by());
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreated_at(Timestamp.valueOf(now));
        newUser.setUpdated_at(Timestamp.valueOf(now));
        if(created_by.isPresent()){
            newUser.setCreated_by(created_by.get());
            newUser.setUpdated_by(created_by.get());
        }
        newUser = userRepo.saveAndFlush(newUser);
        saveUserDetail(userInfo,newUser,admin);
        saveUserRole(userInfo.getRole(),newUser,admin);
        return newUser;
    }

    public boolean resetPassword(String username){
        Users user = userRepo.findUsersByUsername(username).orElse(null);
        if(user==null){
            return false;
        }
        int randomNumber = ThreadLocalRandom.current().nextInt(1000, 100000 + 1);
        user.setPassword(user.getUsername().substring(0,2)+randomNumber);
        userRepo.save(user);
        return true;
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
        userBranch.ifPresent(updateUser::setBranch);
        updateUser.setUsername(updatedUser.getUsername());
        updateUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        updateUser.setSalt(updatedUser.getSalt());
        updateUser.setStatus(updatedUser.getStatus());
        Optional<Users> updated_by = userRepo.findById(updatedUser.getUpdated_by());
        updated_by.ifPresent(updateUser::setUpdated_by);
        updateUser.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        return Optional.of(userRepo.save(updateUser));
    }

    private void saveUserDetail(UsersRequest userInfo,Users newUser,Users admin){
        UserDetail newUserDetail = new UserDetail();
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(now.toLocalDate());
        newUserDetail.setCreated_at(Timestamp.valueOf(now));
        newUserDetail.setUpdated_at(Timestamp.valueOf(now));
        newUserDetail.setStatus(userInfo.getStatus());
        newUserDetail.setFull_name(userInfo.getFullname());
        newUserDetail.setUser(newUser);
        newUserDetail.setStart_date(date);
        newUserDetail.setStatus_changed_on(date);
        newUserDetail.setCreated_by(admin);
        newUserDetail.setUpdated_by(admin);
        newUserDetail.setPhoneNumber(userInfo.getPhoneNumber());
        userDetailRepo.save(newUserDetail);
    }
    private void saveUserRole(Long roleId,Users newUser,Users admin){
        UserRole newUserRole = new UserRole();
        Optional<Role> assignedRole = roleRepo.findById(roleId);
        assignedRole.ifPresent(newUserRole::setRole);
        newUserRole.setAssigned_by(admin);
        newUserRole.setUpdated_by(admin);
        LocalDateTime now = LocalDateTime.now();
        newUserRole.setCreated_at(Timestamp.valueOf(now));
        newUserRole.setUpdated_at(Timestamp.valueOf(now));
        newUserRole.setStatus("Active");
        newUserRole.setUser(newUser);
        userRoleRepo.save(newUserRole);
    }
}
