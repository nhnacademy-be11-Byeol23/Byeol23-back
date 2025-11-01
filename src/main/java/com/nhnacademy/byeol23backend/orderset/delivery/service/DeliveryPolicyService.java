package com.nhnacademy.byeol23backend.orderset.delivery.service;

import java.util.List;

import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;

public interface DeliveryPolicyService {
	List<DeliveryPolicyInfoResponse> getDeliveryPolicies();

	DeliveryPolicyCreateResponse createDeliveryPolicy(DeliveryPolicyCreateRequest request);

	DeliveryPolicyInfoResponse getCurrentDeliveryPolicy();
}
