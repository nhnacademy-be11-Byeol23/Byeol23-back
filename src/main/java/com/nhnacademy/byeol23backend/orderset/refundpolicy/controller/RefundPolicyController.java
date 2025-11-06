package com.nhnacademy.byeol23backend.orderset.refundpolicy.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.service.RefundPolicyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/refund-policies")
@RequiredArgsConstructor
public class RefundPolicyController {
	private final RefundPolicyService refundPolicyService;

	@GetMapping
	public ResponseEntity<Page<RefundPolicyInfoResponse>> getAllRefundPolicies(
		@PageableDefault(size = 10, sort = "changedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<RefundPolicyInfoResponse> responses = refundPolicyService.getAllRefundPolicies(pageable);
		return ResponseEntity.ok(responses);
	}

	@PostMapping
	public ResponseEntity<RefundPolicyCreateResponse> createRefundPolicy(
		@RequestBody RefundPolicyCreateRequest refundPolicyCreateRequest) {
		RefundPolicyCreateResponse response = refundPolicyService.createRefundPolicy(refundPolicyCreateRequest);
		URI uri = URI.create("api/refund-policies/" + response.refundPolicyId());
		return ResponseEntity.created(uri).body(response);
	}

	@GetMapping("/current")
	public ResponseEntity<RefundPolicyInfoResponse> getCurrentRefundPolicy() {
		RefundPolicyInfoResponse response = refundPolicyService.getCurrentRefundPolicy();
		return ResponseEntity.ok(response);
	}

}