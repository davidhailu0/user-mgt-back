package com.hajj.hajj.repository;

import com.hajj.hajj.model.HUjjaj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HujjajRepo extends JpaRepository<HUjjaj, Long> {

    @Query(value = "SELECT h from HUjjaj h where h.payment_code = :payment_code and h.branch_name = :branch_name")
    Optional<HUjjaj> findHUjjajByPaymentCode(@Param("payment_code") String payment_code,@Param("branch_name") String branchName);

    @Query(value = "SELECT h from HUjjaj h where h.paid = :status and h.branch_name = :branch_name")
    List<HUjjaj> findHUjjajByPaidStatus(@Param("status") boolean status,@Param("branch_name") String branchName);

}
