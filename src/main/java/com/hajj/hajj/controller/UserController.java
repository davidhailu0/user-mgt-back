package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.model.Users;
import com.hajj.hajj.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin
@RequestMapping("api/v1/user")
@RestController
public class UserController {
    
    @Autowired
    UserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Users> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<Users> getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Users registerUser(@Valid @RequestBody Users user){
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Optional<Users> updateUserInfo(@RequestBody Users user,@PathVariable Long id){
        return userService.updateUser(id,user);
    }
}
