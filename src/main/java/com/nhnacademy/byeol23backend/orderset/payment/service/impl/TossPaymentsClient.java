package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nhnacademy.byeol23backend.orderset.payment.domain.PaymentProvider;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentConfirmResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentCancelException;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentConfirmException;
import com.nhnacademy.byeol23backend.orderset.payment.exception.TossApiRequestException;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentGatewayClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient implements PaymentGatewayClient {
	private final ObjectMapper objectMapper;

	@Value("${tossPayment.secretKey}")
	private String secretKey;

	@Override
	public PaymentConfirmResponse confirm(PaymentParamRequest request) {
		HttpRequest httpRequest = buildTossConfirmRequest(request);

		try {
			HttpResponse<String> response = HttpClient.newHttpClient()
				.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				throw new PaymentConfirmException("Toss API 결제 승인에 실패했습니다.: " + response.body());
			}

			TossApiConfirmResponse tossDto = objectMapper.readValue(response.body(), TossApiConfirmResponse.class);

			return new PaymentConfirmResponse(tossDto.paymentKey, tossDto.orderId, tossDto.orderName, tossDto.status,
				tossDto.totalAmount, tossDto.requestedAt.toLocalDateTime(), tossDto.approvedAt.toLocalDateTime(),
				tossDto.method);
		} catch (IOException | InterruptedException e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new TossApiRequestException("Toss Api 요청에 실패했습니다.: " + e.getMessage());
		}
	}

	@Override
	public PaymentCancelResponse cancel(PaymentCancelRequest request) {
		HttpRequest httpRequest = buildTossCancelRequest(request);

		try {
			HttpResponse<String> response = HttpClient.newHttpClient()
				.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				throw new PaymentCancelException("Toss Payments Api 결제 취소에 실패했습니다.");
			}

			TossApiCancelResponse tossDto = objectMapper.readValue(response.body(), TossApiCancelResponse.class);

			return new PaymentCancelResponse(tossDto.paymentKey, tossDto.cancelReason);
		} catch (IOException | InterruptedException e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new TossApiRequestException("Toss Api 요청에 실패했습니다.: " + e.getMessage());
		}
	}

	@Override
	public PaymentProvider getProviderType() {
		return PaymentProvider.TOSS_PAYMENTS;
	}

	private HttpRequest buildTossConfirmRequest(PaymentParamRequest request) {
		ObjectNode requestObj = objectMapper.createObjectNode()
			.put("orderId", request.orderId())
			.put("paymentKey", request.paymentKey())
			.put("amount", request.amount());

		String requestBody = null;
		try {
			requestBody = objectMapper.writeValueAsString(requestObj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return HttpRequest.newBuilder()
			.uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
			.header("Authorization", getAuthorizations())
			.header("Content-Type", "application/json")
			.method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
			.build();
	}

	private HttpRequest buildTossCancelRequest(PaymentCancelRequest request) {
		ObjectNode requestObj = objectMapper.createObjectNode()
			.put("cancelReason", request.cancelReason());

		String requestBody = null;
		try {
			requestBody = objectMapper.writeValueAsString(requestObj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return HttpRequest.newBuilder()
			.uri(
				URI.create(
					"https://api.tosspayments.com/v1/payments/" + request.paymentKey() + "/cancel"))
			.header("Authorization", getAuthorizations())
			.header("Content-Type", "application/json")
			.method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
			.build();

	}

	private String getAuthorizations() {
		String originalKey = secretKey + ":";
		String encodedKey = Base64.getEncoder().encodeToString(originalKey.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encodedKey;
	}

	private record TossApiConfirmResponse(String paymentKey,
										  String orderId,
										  String orderName,
										  String status,
										  BigDecimal totalAmount,
										  OffsetDateTime requestedAt,
										  OffsetDateTime approvedAt,
										  String method) {

	}

	private record TossApiCancelResponse(String paymentKey,
										 String cancelReason) {

	}
}
