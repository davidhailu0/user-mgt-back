package com.hajj.hajj.repository;

import com.hajj.hajj.model.HUjjaj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HujjajRepo extends JpaRepository<HUjjaj, Long> {

    @Query(value = "SELECT h from HUjjaj h where h.payment_code = :payment_code")
    Optional<HUjjaj> findHUjjajByPaymentCode(@Param("payment_code") String payment_code);

    @Query(value = "SELECT h from HUjjaj h where h.paid = :status")
    List<HUjjaj> findHUjjajByPaidStatus(@Param("status") boolean status);

}
