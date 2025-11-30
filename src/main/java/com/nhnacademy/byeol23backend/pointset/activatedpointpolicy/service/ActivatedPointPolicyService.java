package com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.domain.ActivatedPointPolicy;
import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.exception.NoActivatedPolicyException;
import com.nhnacademy.byeol23backend.pointset.activatedpointpolicy.repository.ActivatedPointPolicyRepository;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.domain.PointPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.dto.ReservedPolicy;
import com.nhnacademy.byeol23backend.pointset.pointpolicy.repository.PointPolicyRepository;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.domain.PointPolicyType;
import com.nhnacademy.byeol23backend.pointset.pointpolicytype.repository.PointPolicyTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivatedPointPolicyService {
	private final ActivatedPointPolicyRepository activatedPointPolicyRepository;
	private final PointPolicyTypeRepository pointPolicyTypeRepository;
	private final PointPolicyRepository pointPolicyRepository;

	@Transactional
	public void activatePolicy(Long policyId) {
		PointPolicy pointPolicy = pointPolicyRepository.getReferenceById(policyId);
		ActivatedPointPolicy activatedPointPolicy = activatedPointPolicyRepository.findByPointPolicyType(pointPolicy.getPointPolicyType());
		if (activatedPointPolicy != null) activatedPointPolicyRepository.delete(activatedPointPolicy);
		ActivatedPointPolicy newActivatedPolicy = new ActivatedPointPolicy(pointPolicy);
		activatedPointPolicyRepository.save(newActivatedPolicy);
	}

	@Transactional(readOnly = true)
	public PointPolicy getActivatedPolicy(ReservedPolicy policyType) {
		PointPolicyType pointPolicyType = pointPolicyTypeRepository.getReferenceById(policyType);
		ActivatedPointPolicy activatedPointPolicy = activatedPointPolicyRepository.findByPointPolicyType(pointPolicyType);
		if (activatedPointPolicy == null) {
			throw new NoActivatedPolicyException("No activated policy for type: " + policyType);
		}
		return activatedPointPolicy.getPointPolicy();
	}

}
