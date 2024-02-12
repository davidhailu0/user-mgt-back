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

    List<UserDetail> findUserDetailByPhoneNumberContaining(@Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT u from UserDetail u where u.user.branch.name = :branchName")
    List<UserDetail> findUsersByBranch(@Param("branchName") String branch);

    @Query(value = "SELECT u from UserDetail u where u.user.status = 'Inactive'")
    List<UserDetail> findUnapprovedData();

    @Query(value = "SELECT u from UserDetail u where u.user.status = 'Inactive' and u.user.branch.name= :branchName")
    List<UserDetail> findUnapprovedDataWithBranch(@Param("branchName") String branch);
}
