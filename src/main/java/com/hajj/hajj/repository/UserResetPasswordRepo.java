package com.hajj.hajj.repository;

import com.hajj.hajj.model.Message;
import com.hajj.hajj.model.UserResetPassword;
import com.hajj.hajj.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserResetPasswordRepo extends JpaRepository<UserResetPassword,Long> {
    @Query(value = "SELECT u from UserResetPassword u where u.checker IS NULL")
    List<UserResetPassword> findUserResetRequest();
}
