package com.hajj.hajj.repository;

import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HujjajRepo extends JpaRepository<HUjjaj, Long> {

    @Query(value = "SELECT h from HUjjaj h where h.payment_code = :payment_code and h.branch_name = :branch_name")
    Optional<HUjjaj> findHUjjajByPaymentCode(@Param("payment_code") String payment_code,@Param("branch_name") String branchName);

    @Query(value = "SELECT h from HUjjaj h where h.payment_code = :payment_code")
    Optional<HUjjaj> findHUjjajByPaymentCode(@Param("payment_code") String payment_code);
    @Query(value = "SELECT h from HUjjaj h where h.paid = :status and h.branch_name = :branch_name")
    List<HUjjaj> findHUjjajByPaidStatus(@Param("status") boolean status,@Param("branch_name") String branchName);

    @Query(value = "SELECT h from HUjjaj h where h.branch_name = :branch_name")
    List<HUjjaj> getDashboardData(@Param("branch_name") String branchName);

    @Query(value = "SELECT h from HUjjaj h where h.paid = false and h.branch_name = :branch_name")
    List<HUjjaj> getUnauthorizedTransactions(@Param("branch_name") String branchName);

    @Query(value = "SELECT h from HUjjaj h where h.Maker_Id = :Maker_Id and h.branch_name = :branch_name")
    List<HUjjaj> getMadeHujjajList(@Param("Maker_Id") Users maker_id,@Param("branch_name") String branch_name);

    @Query(value = "SELECT h from HUjjaj h where h.branch_name = :branch_name")
    List<HUjjaj> getCheckedHujjajList(@Param("branch_name") String branch_name);

    @Query(value = "SELECT h from HUjjaj h where h.trans_ref_no = :transactionRef")
    Optional<HUjjaj> checkHajjData(@Param("transactionRef") String transactionRef);

    @Query(value = "SELECT h from HUjjaj h where h.isFromMobile = true and h.branch_name = :branch_name")
    List<HUjjaj> getMobileHajjData(@Param("branch_name") String branch_name);

    @Query(value = "SELECT h from HUjjaj h where h.isFromMobile = :isFromMobile")
    List<HUjjaj> mobileHajjData(@Param("isFromMobile") boolean isFromMobile);
}
