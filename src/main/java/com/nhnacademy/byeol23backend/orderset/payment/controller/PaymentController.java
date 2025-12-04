package com.nhnacademy.byeol23backend.orderset.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentResultResponse;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public ResponseEntity<PaymentResultResponse> confirmPayment(CustomerIdentifier identifier,
		@RequestBody PaymentParamRequest paymentParamRequest) {
		PaymentResultResponse response = paymentService.confirmPayment(identifier, paymentParamRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/cancel")
	public ResponseEntity<PaymentCancelResponse> cancelPayment(@RequestBody PaymentCancelRequest paymentCancelRequest) {
		PaymentCancelResponse response = paymentService.cancelPayment(paymentCancelRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public void createPayment(@RequestBody PaymentResultResponse response) {
		paymentService.createPayment(response);
	}

}
