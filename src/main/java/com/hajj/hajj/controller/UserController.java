package com.hajj.hajj.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<Users> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    
    public Users getUserById(@PathVariable Long id){
        return userService.getUserById(id).get();
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Users registerUser(@Valid @RequestBody UsersRequest user,HttpServletRequest request){
        Users admin = getUser(request);
        if(admin==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userService.saveUser(user,admin);
    }

    @PutMapping("/{id}")
    public Users updateUserInfo(@RequestBody UsersRequest user,@PathVariable Long id){
        return userService.updateUser(id,user).get();
    }

    @PutMapping("/resetPassword/${username}")
    public Object resetPassword(@PathVariable String username){
        boolean status = userService.resetPassword(username);
        if(status){
            Map<String,Object> error = new HashMap<>();
            error.put("status",false);
            error.put("error","The Username does not exist");
            return error;
        }
        Map<String,Object> success = new HashMap<>();
        success.put("status",true);
        success.put("Message","Password has been reset");
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
