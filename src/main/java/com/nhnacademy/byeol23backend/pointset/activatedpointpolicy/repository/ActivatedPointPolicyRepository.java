package com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.domain.ActivatedPointPolicy;
import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.domain.ActivatedPointPolicyId;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain.PointPolicyType;

public interface ActivatedPointPolicyRepository extends JpaRepository<ActivatedPointPolicy, ActivatedPointPolicyId> {
	ActivatedPointPolicy findByPointPolicyType(PointPolicyType pointPolicyType);
}
