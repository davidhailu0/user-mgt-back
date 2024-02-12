package com.hajj.hajj.repository;

import com.hajj.hajj.model.UserResetPassword;
import com.hajj.hajj.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserResetPasswordRepo extends JpaRepository<UserResetPassword,Long> {
    @Query(value = "SELECT u from UserResetPassword u where u.reset_user = :user")
    Optional<UserResetPassword> findUserResetDetailByUser(@Param("user") Users user);
}
