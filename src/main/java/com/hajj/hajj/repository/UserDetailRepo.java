package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.UserDetail;

@Repository
public interface UserDetailRepo extends JpaRepository<UserDetail,Long> {
    
}
