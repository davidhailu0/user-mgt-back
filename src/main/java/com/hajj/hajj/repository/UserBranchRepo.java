package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.UserBranch;

@Repository
public interface UserBranchRepo extends JpaRepository<UserBranch,Long>{
    
}
