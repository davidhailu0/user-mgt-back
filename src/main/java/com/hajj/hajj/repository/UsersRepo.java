package com.hajj.hajj.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hajj.hajj.model.Users;

public interface UsersRepo extends JpaRepository<Users,Long> {
  Optional<Users> findUsersByUsername(String username);

}
