package com.hajj.hajj.repository;

import java.util.List;
import java.util.Optional;

import com.hajj.hajj.model.HUjjaj;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hajj.hajj.model.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersRepo extends JpaRepository<Users,Long> {
  @Query(value = "SELECT u from Users u where u.username = :username")
  Optional<Users> findUsersByUsername(@Param("username") String username);


}
