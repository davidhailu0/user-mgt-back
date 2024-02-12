package com.hajj.hajj.repository;

import com.hajj.hajj.model.UserUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserUpdateRepo extends JpaRepository<UserUpdate,Long> {
}
