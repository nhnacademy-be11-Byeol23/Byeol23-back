package com.nhnacademy.byeol23backend.orderset.refundpolicy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundOption;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundPolicy;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {
	@Query("SELECT p FROM RefundPolicy p WHERE p.refundOption = :option")
	Optional<RefundPolicy> findByRefundOption(@Param("option") RefundOption option);
}
