package com.nhnacademy.byeol23backend.orderset.payment.controller;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	@PostMapping("/confirm")
	public ResponseEntity<String> confirmPayment(@RequestBody PaymentParamRequest paymentParamRequest) {
		try {
			HttpResponse<String> response = paymentService.requestConfirm(paymentParamRequest);

			return ResponseEntity.status(response.statusCode()).body(response.body());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/api/payments/cancel")
	ResponseEntity<String> cancelPayment(@RequestBody PaymentCancelRequest paymentCancelRequest) {
		try {
			HttpResponse<String> response = paymentService.requestCancel(paymentCancelRequest);
			return ResponseEntity.status(response.statusCode()).body(response.body());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
