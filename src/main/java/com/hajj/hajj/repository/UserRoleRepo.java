package com.hajj.hajj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.model.Users;

import java.util.Optional;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole,Long> {
    Optional<UserRole> findByUser(Users user);
}