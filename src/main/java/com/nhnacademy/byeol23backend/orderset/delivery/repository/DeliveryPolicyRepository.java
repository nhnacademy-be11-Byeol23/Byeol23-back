package com.nhnacademy.byeol23backend.orderset.delivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {
	Optional<DeliveryPolicy> findFirstByOrderByChangedAtDesc();
}
