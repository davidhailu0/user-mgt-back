package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.Branch;

@Repository
public interface BranchRepo extends JpaRepository<Branch,Long>{
    
}
