package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.Permission;

@Repository
public interface PermissionRepo extends JpaRepository<Permission,Long>{
    
}
