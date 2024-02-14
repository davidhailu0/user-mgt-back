package com.hajj.hajj.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hajj.hajj.DTO.ResetPasswordDTO;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.UserDetail;
import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.repository.UsersRepo;
import com.hajj.hajj.service.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.UsersRequest;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin("*")
@RequestMapping("api/v1/user")
@RestController
public class UserController {
    
    @Autowired
    UserService userService;

    @Autowired
    JWTUtil util;

    @Autowired
    UsersRepo usersRepo;


    @Autowired
    LoggerService loggerService;

    @Autowired
    ObjectMapper objectMapper;


    @GetMapping
    public List<UserDetail> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping("/branch")
    public List<UserDetail> getUsersByBranch(@RequestBody Map<String,String> requestBody){
        return userService.getUsersByBranch(requestBody.get("branchName"));
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable Long id,HttpServletRequest request) throws JsonProcessingException {
        Users user = getUser(request);
        loggerService.createNewLog(user,request.getRequestURI(),objectMapper.writeValueAsString(user));
        return userService.getUserById(id).get();
    }

    @PreAuthorize("hasRole('superadmin')")
    @GetMapping("/unapprovedUsers/{branchName}")
    public Object listOfUnapprovedUser(@PathVariable String branchName,HttpServletRequest request){
        Users user = getUser(request);
        return userService.allUnapprovedUsers(branchName);
    }

    @PreAuthorize("hasRole('superadmin')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Object registerUser(@Valid @RequestBody UsersRequest user,HttpServletRequest request) throws JsonProcessingException {
        Users admin = getUser(request);
        if(admin==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Object resp = userService.saveUser(user,admin);
        loggerService.createNewLog(admin,request.getRequestURI(), objectMapper.writeValueAsString(resp));
        return resp;
    }

    @PreAuthorize("hasRole('superadmin')")
    @GetMapping("/approve/{id}")
    public Object approveUser(@PathVariable Long id,HttpServletRequest request){
        Users user = getUser(request);
        Object resp = userService.approveUser(id,user);
        loggerService.createNewLog(user,request.getRequestURI(),resp.toString());
        return resp;
    }

    @PutMapping("/changePassword")
    public Object changePassword(HttpServletRequest request,@RequestBody ResetPasswordDTO resetPasswordDTO) throws JsonProcessingException {
        Users user = getUser(request);
        Object resp = userService.changePassword(user,resetPasswordDTO);
        loggerService.createNewLog(user,request.getRequestURI(),objectMapper.writeValueAsString(resp));
        return resp;
    }

    @PutMapping("/{id}")
    public Object updateUserInfo(@RequestBody UsersRequest user,@PathVariable Long id,HttpServletRequest request) throws JsonProcessingException {
        Users admin = getUser(request);
        Object resp = userService.updateUser(id,user,admin);
        loggerService.createNewLog(admin,request.getRequestURI(), objectMapper.writeValueAsString(resp));
        return resp;
    }

    @PreAuthorize("hasRole('superadmin')")
    @PutMapping("/resetPassword/{username}")
    public Object resetPassword(@PathVariable String username,HttpServletRequest request){
        Users user = getUser(request);
        boolean status = userService.resetPassword(username,user);
        if(!status){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","The User is not activated");
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
        }
        Map<String,Object> success = new HashMap<>();
        success.put("status",true);
        success.put("Message","Password has been reset");
        loggerService.createNewLog(user,request.getRequestURI(),success.toString());
        return success;
    }
    Users getUser(HttpServletRequest request){
        String jwtToken = request.getHeader("Authorization");
        if(jwtToken==null||!jwtToken.startsWith("Bearer")){
            return null;
        }
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        return usersRepo.findUsersByUsername(username).get();
    }
}
