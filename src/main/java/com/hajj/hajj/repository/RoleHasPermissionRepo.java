package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.RoleHasPermission;

@Repository
public interface RoleHasPermissionRepo extends JpaRepository<RoleHasPermission,Long>{
    
}
