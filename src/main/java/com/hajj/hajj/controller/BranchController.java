package com.hajj.hajj.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UsersRepo;
import com.hajj.hajj.service.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.BranchRequest;
import com.hajj.hajj.model.Branch;
import com.hajj.hajj.service.BranchService;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/branch")
public class BranchController{
    @Autowired
    BranchService branchService;

    @Autowired
    LoggerService loggerService;

    @Autowired
    JWTUtil util;
    @Autowired
    UsersRepo usersRepo;


    @Autowired
    ObjectMapper objectMapper;
    @GetMapping
    public List<Branch> getAllBranches(HttpServletRequest request) throws JsonProcessingException {
        Users user = getUser(request);
        //loggerService.createNewLog(user,request.getRequestURI(),objectMapper.writeValueAsString(branchService.getAllBranches()));
        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public Optional<Branch> getBranchById(@PathVariable Long id, HttpServletRequest request) throws JsonProcessingException {
            Users user = getUser(request);
            loggerService.createNewLog(user,request.getRequestURI(),objectMapper.writeValueAsString(branchService.getBranchById(id)));
            return branchService.getBranchById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Branch registerNewBranch(@Valid @RequestBody BranchRequest newBranch,HttpServletRequest request) throws JsonProcessingException {
        Users user = getUser(request);
        loggerService.createNewLog(user,request.getRequestURI(),objectMapper.writeValueAsString(branchService.saveBranch(newBranch)));
        return branchService.saveBranch(newBranch);
    }

    @PutMapping("/{id}")
    public Optional<Branch> updateBranch(@RequestBody BranchRequest updatedBranch,@PathVariable Long id,HttpServletRequest request) throws JsonProcessingException {
        Users user = getUser(request);
        loggerService.createNewLog(user,request.getRequestURI(),objectMapper.writeValueAsString(branchService.updateBranch(id,updatedBranch)));
        return branchService.updateBranch(id,updatedBranch);
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