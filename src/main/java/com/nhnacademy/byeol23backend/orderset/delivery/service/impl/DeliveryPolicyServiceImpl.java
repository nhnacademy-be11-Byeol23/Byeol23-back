package com.nhnacademy.byeol23backend.orderset.delivery.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhnacademy.byeol23backend.orderset.delivery.domain.DeliveryPolicy;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.exception.DeliveryPolicyNotFoundException;
import com.nhnacademy.byeol23backend.orderset.delivery.repository.DeliveryPolicyRepository;
import com.nhnacademy.byeol23backend.orderset.delivery.service.DeliveryPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryPolicyServiceImpl implements DeliveryPolicyService {
	private final DeliveryPolicyRepository deliveryPolicyRepository;

	@Override
	public Page<DeliveryPolicyInfoResponse> getDeliveryPolicies(Pageable pageable) {
		Page<DeliveryPolicy> deliveryPoliciesPage = deliveryPolicyRepository.findAll(pageable);

		return deliveryPoliciesPage.map(deliveryPolicy -> new DeliveryPolicyInfoResponse(
			deliveryPolicy.getFreeDeliveryCondition(),
			deliveryPolicy.getDeliveryFee(),
			deliveryPolicy.getChangedAt()
		));
	}

	@Override
	public DeliveryPolicyCreateResponse createDeliveryPolicy(DeliveryPolicyCreateRequest request) {
		DeliveryPolicy deliveryPolicy = new DeliveryPolicy(request.freeDeliveryCondition(),
			request.deliveryFee(),
			LocalDateTime.now());

		deliveryPolicyRepository.save(deliveryPolicy);

		return new DeliveryPolicyCreateResponse(deliveryPolicy.getDeliveryPolicyId(),
			deliveryPolicy.getFreeDeliveryCondition(), deliveryPolicy.getDeliveryFee(), deliveryPolicy.getChangedAt());
	}

	@Override
	public DeliveryPolicyInfoResponse getCurrentDeliveryPolicy() {
		DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findFirstByOrderByChangedAtDesc()
			.orElseThrow(() -> new DeliveryPolicyNotFoundException("정책 아이디를 찾을 수 없습니다."));

		return DeliveryPolicyInfoResponse.of(deliveryPolicy.getFreeDeliveryCondition(),
			deliveryPolicy.getDeliveryFee());
	}

}
