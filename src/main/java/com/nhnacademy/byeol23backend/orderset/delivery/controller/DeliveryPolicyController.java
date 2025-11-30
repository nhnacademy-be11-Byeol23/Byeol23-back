package com.nhnacademy.byeol23backend.orderset.delivery.controller;

import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.service.DeliveryPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/delivery-policies")
@RequiredArgsConstructor
public class DeliveryPolicyController {
	private final DeliveryPolicyService deliveryPolicyService;

	@GetMapping
	public ResponseEntity<Page<DeliveryPolicyInfoResponse>> getDeliveryPolicies(Pageable pageable) {
		Page<DeliveryPolicyInfoResponse> responses = deliveryPolicyService.getDeliveryPolicies(pageable);
		return ResponseEntity.ok(responses);
	}

	@PostMapping
	public ResponseEntity<DeliveryPolicyCreateResponse> createDeliveryPolicy(
		@RequestBody DeliveryPolicyCreateRequest request) {
		DeliveryPolicyCreateResponse response = deliveryPolicyService.createDeliveryPolicy(request);
		URI uri = URI.create("/api/delivery-policies/current");
		return ResponseEntity.created(uri).body(response);
	}

	@GetMapping("/current")
	ResponseEntity<DeliveryPolicyInfoResponse> getCurrentDeliveryPolicy() {
		DeliveryPolicyInfoResponse response = deliveryPolicyService.getCurrentDeliveryPolicy();
		return ResponseEntity.ok(response);
	}

}