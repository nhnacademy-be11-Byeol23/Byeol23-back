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

@Service
@RequiredArgsConstructor
public class ActivatedPointPolicyService {
	private final ActivatedPointPolicyRepository activatedPointPolicyRepository;
	private final PointPolicyTypeRepository pointPolicyTypeRepository;
	private final PointPolicyRepository pointPolicyRepository;

	protected void activatePolicy(PointPolicy pointPolicy) {
		ActivatedPointPolicy activatedPointPolicy = activatedPointPolicyRepository.findByPointPolicyType(pointPolicy.getPointPolicyType());
		if (activatedPointPolicy != null) {
			activatedPointPolicyRepository.delete(activatedPointPolicy);
		}
		ActivatedPointPolicy newActivatedPolicy = new ActivatedPointPolicy(pointPolicy);
		activatedPointPolicyRepository.save(newActivatedPolicy);
	}

	@Transactional //PointPolicy를 활성화
	public void activatePolicy(Long policyId) {
		PointPolicy pointPolicy = pointPolicyRepository.getReferenceById(policyId);
		activatePolicy(pointPolicy);
	}

	public PointPolicy getActivatedPolicy(ReservedPolicy policyType) {
		PointPolicyType pointPolicyType = pointPolicyTypeRepository.findById(policyType)
			.orElseThrow(() -> new NoActivatedPolicyException("Invalid Point Policy Type: " + policyType));
		return pointPolicyType.getActivatedPointPolicy().getPointPolicy();
	}

}
