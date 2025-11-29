package com.nhnacademy.byeol23backend.pointset.pointpolicytype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain.PointPolicyType;

public interface PointPolicyTypeRepository extends JpaRepository<PointPolicyType, ReservedPolicy> {
	@Query("SELECT p FROM PointPolicyType p LEFT JOIN FETCH p.pointPolicies")
	java.util.List<PointPolicyType> findAllWithPointPolicies();
}
