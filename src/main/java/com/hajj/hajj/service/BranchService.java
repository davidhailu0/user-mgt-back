package com.hajj.hajj.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import com.hajj.hajj.DTO.BranchRequest;
import com.hajj.hajj.model.Branch;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.BranchRepo;
import com.hajj.hajj.repository.UsersRepo;

@Service
public class BranchService {
    @Autowired
    BranchRepo branchRepo;

    @Autowired
    UsersRepo usersRepo;

    public List<Branch> getAllBranches(){
        return branchRepo.findAll();
    }

    public Optional<Branch> getBranchById(Long id){
        return branchRepo.findById(id);
    }

    public Branch saveBranch(BranchRequest branchInfo){
        Branch newBranch = new Branch();
        newBranch.setBranch_code(branchInfo.getBranch_code());
        newBranch.setName(branchInfo.getName());
        LocalDateTime now = LocalDateTime.now();
        newBranch.setCreated_at(Timestamp.valueOf(now));
        newBranch.setUpdated_at(Timestamp.valueOf(now));
        Optional<Users> created_by = usersRepo.findById(branchInfo.getCreated_by());
        if(created_by.isPresent()){
            newBranch.setCreated_by(created_by.get());
            newBranch.setUpdated_by(created_by.get());
        }
        newBranch.setStatus(branchInfo.getStatus());
        return branchRepo.save(newBranch);
    }

    public Optional<Branch> updateBranch(Long id,BranchRequest updateBranchInfo){
        if(!branchRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Branch With ID of "+id.toString()+" Not Found");
        } 
        Branch updatedBranch = branchRepo.findById(id).get();
        updatedBranch.setBranch_code(updateBranchInfo.getBranch_code());
        updatedBranch.setName(updateBranchInfo.getName());
        updatedBranch.setStatus(updateBranchInfo.getStatus());
        updatedBranch.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        Optional<Users> updated_by = usersRepo.findById(updateBranchInfo.getUpdated_by());
        if(updated_by.isPresent()){
            updatedBranch.setUpdated_by(updated_by.get());
        }
        return Optional.of(branchRepo.save(updatedBranch));
    }
}
