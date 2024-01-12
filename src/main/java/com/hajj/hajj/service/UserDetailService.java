package com.hajj.hajj.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.DTO.UserDetailRequest;
import com.hajj.hajj.model.UserDetail;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UserDetailRepo;
import com.hajj.hajj.repository.UsersRepo;

@Service
public class UserDetailService {
    @Autowired
    UserDetailRepo userDetailRepo;

    @Autowired
    UsersRepo usersRepo;

    public List<UserDetail> getAllUserDetail(){
        return userDetailRepo.findAll();
    }

    public Optional<UserDetail> getUserDetailById(@PathVariable Long id){
        return userDetailRepo.findById(id);
    }

    public UserDetail saveUserDetail(UserDetailRequest userDetail){
        UserDetail newUserDetail = new UserDetail();
        newUserDetail.setFull_name(userDetail.getFull_name());
        newUserDetail.setStatus(userDetail.getStatus());
        LocalDateTime now = LocalDateTime.now();
        Optional<Users> created_by = usersRepo.findById(userDetail.getCreated_by());
        if(created_by.isPresent()){
            newUserDetail.setCreated_by(created_by.get());
            newUserDetail.setUpdated_by((created_by.get()));
        }
        newUserDetail.setCreated_at(Timestamp.valueOf(now));
        newUserDetail.setUpdated_at(Timestamp.valueOf(now));
        newUserDetail.setStatus_changed_on(Date.valueOf(LocalDate.now()));
        newUserDetail.setStart_date(Date.valueOf(LocalDate.now()));
        return userDetailRepo.save(newUserDetail);
    }

    public Optional<UserDetail> updateUserDetail(Long id,UserDetailRequest userDetail){
       if(!userDetailRepo.existsById(id)){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Detail With ID of "+id.toString()+" Not Found");
       }
        UserDetail updatedUserDetail =userDetailRepo.findById(id).get();
        updatedUserDetail.setFull_name(userDetail.getFull_name());
        Optional<Users> updated_by = usersRepo.findById(userDetail.getUpdated_by());
        if(updated_by.isPresent()){
            updatedUserDetail.setUpdated_by((updated_by.get()));
        }
        updatedUserDetail.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        if(!updatedUserDetail.getStatus().equals(userDetail.getStatus())){
            updatedUserDetail.setStatus_changed_on(Date.valueOf(LocalDate.now()));
        }
        updatedUserDetail.setStatus(userDetail.getStatus());
       return Optional.of(userDetailRepo.save(updatedUserDetail));
    }
}
