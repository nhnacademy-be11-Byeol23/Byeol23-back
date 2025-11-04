package com.nhnacademy.byeol23backend.orderset.refundpolicy.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundPolicy;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.exception.RefundPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.repository.RefundPolicyRepository;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.service.RefundPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundPolicyServiceImpl implements RefundPolicyService {
	private final RefundPolicyRepository refundPolicyRepository;

	@Override
	public Page<RefundPolicyInfoResponse> getAllRefundPolicies(Pageable pageable) {
		Page<RefundPolicy> refundPolicies = refundPolicyRepository.findAll(pageable);

		return refundPolicies.map(refundPolicy -> new RefundPolicyInfoResponse(
			refundPolicy.getRefundPolicyName(),
			refundPolicy.getRefundCondition(),
			refundPolicy.getComment(),
			refundPolicy.getChangedAt()
		));
	}

	@Override
	public RefundPolicyCreateResponse createRefundPolicy(RefundPolicyCreateRequest refundPolicyCreateRequest) {
		LocalDateTime now = LocalDateTime.now();
		RefundPolicy refundPolicy = RefundPolicy.of(refundPolicyCreateRequest.refundPolicyName(),
			refundPolicyCreateRequest.refundCondition(),
			refundPolicyCreateRequest.comment(), now);

		refundPolicyRepository.save(refundPolicy);

		return new RefundPolicyCreateResponse(refundPolicy.getRefundPolicyId(), refundPolicy.getRefundPolicyName(),
			refundPolicy.getRefundCondition(), refundPolicy.getComment(), now);
	}

	@Override
	public RefundPolicyInfoResponse getCurrentRefundPolicy() {
		RefundPolicy currentRefundPolicy = refundPolicyRepository.findFirstByOrderByChangedAtDesc()
			.orElseThrow(() -> new RefundPolicyNotFoundException("반품 정책이 존재하지 않습니다."));

		return new RefundPolicyInfoResponse(currentRefundPolicy.getRefundPolicyName(),
			currentRefundPolicy.getRefundCondition(),
			currentRefundPolicy.getComment(), currentRefundPolicy.getChangedAt());
	}
}
