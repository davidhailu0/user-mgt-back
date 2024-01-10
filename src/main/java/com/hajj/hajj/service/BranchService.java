package com.hajj.hajj.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.model.Branch;
import com.hajj.hajj.repository.BranchRepo;

@Service
public class BranchService {
    @Autowired
    BranchRepo branchRepo;

    public List<Branch> getAllBranches(){
        return branchRepo.findAll();
    }

    public Optional<Branch> getBranchById(Long id){
        return branchRepo.findById(id);
    }

    public Branch saveBranch(Branch branchInfo){
        return branchRepo.save(branchInfo);
    }

    public Optional<Branch> updateBranch(Long id,Branch updateBranchInfo){
        if(!branchRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Branch With ID of "+id.toString()+" Not Found");
        }
        return Optional.of(branchRepo.save(updateBranchInfo));
    }
}
