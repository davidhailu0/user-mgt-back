package com.hajj.hajj.service;

import com.hajj.hajj.DTO.UsersRequest;
import com.hajj.hajj.model.Branch;
import com.hajj.hajj.model.UserDetail;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.BranchRepo;
import com.hajj.hajj.repository.UserDetailRepo;
import com.hajj.hajj.repository.UsersRepo;
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

    public Users saveUser(@NonNull UsersRequest userInfo){
        Users newUser = new Users();
        Optional<Branch> userBranch = branchRepo.findById(userInfo.getBranch());
        userBranch.ifPresent(newUser::setBranch);
        newUser.setUsername(userInfo.getUsername());
        newUser.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        newUser.setSalt(userInfo.getSalt());
        newUser.setStatus(userInfo.getStatus());
        Optional<Users> created_by = userRepo.findById(userInfo.getCreated_by());
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(now.toLocalDate());
        newUser.setCreated_at(Timestamp.valueOf(now));
        newUser.setUpdated_at(Timestamp.valueOf(now));
        UserDetail newUserDetail = new UserDetail();
        newUserDetail.setFull_name(userInfo.getFullname());
        newUserDetail.setUser(newUser);
        newUserDetail.setStart_date(date);
        newUserDetail.setStatus_changed_on(date);
        if(created_by.isPresent()){
            newUser.setCreated_by(created_by.get());
            newUser.setUpdated_by(created_by.get());
            newUserDetail.setCreated_by(created_by.get());
            newUserDetail.setUpdated_by(created_by.get());
        }
        userDetailRepo.save(newUserDetail);
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
}
