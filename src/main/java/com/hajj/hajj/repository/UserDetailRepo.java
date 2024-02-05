package com.hajj.hajj.repository;

import com.hajj.hajj.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hajj.hajj.model.UserDetail;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDetailRepo extends JpaRepository<UserDetail,Long> {
    @Query(value = "SELECT u from UserDetail u where u.user = :user")
    Optional<UserDetail> findUserDetailByUser(@Param("user") Users user);

    Optional<UserDetail> findUserDetailByPhoneNumberContaining(@Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT u from UserDetail u where u.user.branch.name = :branchName and u.user.role.name != 'superadmin'")
    List<UserDetail> findUsersByBranch(@Param("branchName") String branch);
}
