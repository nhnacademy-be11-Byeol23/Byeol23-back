package com.nhnacademy.byeol23backend.orderset.delivery.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;

public interface DeliveryPolicyService {
	Page<DeliveryPolicyInfoResponse> getDeliveryPolicies(Pageable pageable);

	DeliveryPolicyCreateResponse createDeliveryPolicy(DeliveryPolicyCreateRequest request);

	DeliveryPolicyInfoResponse getCurrentDeliveryPolicy();
}
