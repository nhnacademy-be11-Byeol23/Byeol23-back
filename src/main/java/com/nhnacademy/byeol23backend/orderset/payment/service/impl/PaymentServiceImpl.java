package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.repository.PaymentRepository;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private final PaymentRepository paymentRepository;
	private final ObjectMapper objectMapper;
	@Value("${tossPayment.secretKey}")
	private String secretKey;

	@Override
	public HttpResponse requestConfirm(PaymentParamRequest paymentParamRequest) throws
		IOException,
		InterruptedException {
		// 승인 요청에 사용할 json 객체 생성
		JsonNode requestObj = objectMapper.createObjectNode()
			.put("orderId", paymentParamRequest.orderId())
			.put("paymentKey", paymentParamRequest.paymentKey())
			.put("amount", paymentParamRequest.amount());

		// ObjectMapper를 이용해 Json 객체를 문자열로 변환
		String requestBody = objectMapper.writeValueAsString(requestObj);

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
			.header("Authorization", getAuthorizations())
			.header("Content-Type", "application/json")
			.method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
			.build();

		return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
	}

	@Override
	public HttpResponse requestCancel(PaymentCancelRequest paymentCancelRequest) throws
		IOException,
		InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(
				URI.create("https://api.tosspayments.com/v1/payments/" + paymentCancelRequest.paymentKey() + "/cancel"))
			.header("Authorization", getAuthorizations())
			.header("Content-Type", "application/json")
			.method("POST",
				HttpRequest.BodyPublishers.ofString("{" + paymentCancelRequest.cancelReason() + "cancelReason}"))
			.build();

		return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
	}

	private String getAuthorizations() {
		String originalKey = secretKey + ":";
		String encodedKey = Base64.getEncoder().encodeToString(originalKey.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encodedKey;
	}
}
