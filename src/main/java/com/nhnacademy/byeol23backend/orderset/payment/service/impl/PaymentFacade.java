package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nhnacademy.byeol23backend.orderset.payment.domain.PaymentProvider;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentConfirmResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentGatewayClient;

@Component
public class PaymentFacade {

	// (PG사 Enum을 key로, 실제 client 구현체를 value로 갖는 Map)
	private final Map<PaymentProvider, PaymentGatewayClient> clientMap;

	/*
	 * Spring이 PaymentGatewayClient를 구현한 모든 Bean(Toss, ....)을
	 * List로 주입
	 * */
	@Autowired
	public PaymentFacade(List<PaymentGatewayClient> clients) {
		this.clientMap = clients.stream()
			.collect(Collectors.toUnmodifiableMap(
				PaymentGatewayClient::getProviderType, // key: TOSS_PAYMENTS
				Function.identity()                    // value : TossPaymentsClient 인스턴스
			));
	}

	/*
	 * 결제 승인을 요청하는 유일한 창구(facade) 메서드
	 * */
	public PaymentConfirmResponse confirmPayment(PaymentProvider provider, PaymentParamRequest request) {
		// 1. Map에서 요청에 맞는 PG사 클라이언트(전략)를 찾음
		PaymentGatewayClient client = clientMap.get(provider);

		if (client == null) {
			throw new IllegalArgumentException("지원하지 않는 결제 프로바이더입니다: " + provider);
		}

		// 2. 실제 작업은 해당 클라이언트에게 위임
		return client.confirm(request);
	}

	/**
	 * 결제 취소를 요청하는 유일한 창구(Facade) 메서드
	 */
	public PaymentCancelResponse cancelPayment(PaymentProvider provider, PaymentCancelRequest request) {
		PaymentGatewayClient client = clientMap.get(provider);
		if (client == null) {
			throw new IllegalArgumentException("지원하지 않는 결제 프로바이더입니다: " + provider);
		}
		return client.cancel(request);
	}

}
