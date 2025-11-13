package com.nhnacademy.byeol23backend.pointset.pointpolicy.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Integer> {
	@Cacheable("value = 'pointPolicies', key = '#pointPolicyName'")
	Optional<PointPolicy> findByPointPolicyName(String pointPolicyName);

	void deleteByPointPolicyName(String pointPolicyName);
}

