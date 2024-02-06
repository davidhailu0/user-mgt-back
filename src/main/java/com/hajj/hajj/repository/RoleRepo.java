package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
    
}
