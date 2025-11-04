package com.nhnacademy.byeol23backend.orderset.refundpolicy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyInfoResponse;

public interface RefundPolicyService {
	Page<RefundPolicyInfoResponse> getAllRefundPolicies(Pageable pageable);

	RefundPolicyCreateResponse createRefundPolicy(RefundPolicyCreateRequest refundPolicyCreateRequest);

	RefundPolicyInfoResponse getCurrentRefundPolicy();
}
