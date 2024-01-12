package com.hajj.hajj.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.BranchRequest;
import com.hajj.hajj.model.Branch;
import com.hajj.hajj.service.BranchService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/branch")
public class BranchController{
    @Autowired
    BranchService branchService;
    
    @GetMapping
    public List<Branch> getAllBranches(){
        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public Optional<Branch> getBranchById(@PathVariable Long id){
        return branchService.getBranchById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Branch registerNewBranch(@Valid @RequestBody BranchRequest newBranch){
        return branchService.saveBranch(newBranch);
    }

    @PutMapping("/{id}")
    public Optional<Branch> updateBranch(@RequestBody BranchRequest updatedBranch,@PathVariable Long id){
        return branchService.updateBranch(id,updatedBranch);
    }
}