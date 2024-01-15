package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.UserDetailRequest;
import com.hajj.hajj.model.UserDetail;
import com.hajj.hajj.service.UserDetailService;

import jakarta.validation.Valid;

@RequestMapping("/api/v1/userDetail")
@RestController
public class UserDetailController {
    @Autowired
    UserDetailService userDetailService;

    @GetMapping
    public List<UserDetail> getAllUserDetail(){
        return userDetailService.getAllUserDetail();
    }

    @GetMapping("/{id}")
    public Optional<UserDetail> getUserDetailById(@PathVariable Long id){
        return userDetailService.getUserDetailById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDetail registerNewUserDetail(@Valid @RequestBody UserDetailRequest userDetail){
        return userDetailService.saveUserDetail(userDetail);
    }

    @PutMapping("/{id}")
    public Optional<UserDetail> updateUserDetail(@RequestBody UserDetailRequest userDetail,@PathVariable Long id){
        return userDetailService.updateUserDetail(id,userDetail);
    }
}
