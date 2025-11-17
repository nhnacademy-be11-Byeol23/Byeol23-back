package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.byeol23backend.orderset.payment.domain.PaymentProvider;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentConfirmResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentGatewayClient;

/**
 * PaymentFacade의 핵심 역할인 '전략 위임'을 테스트합니다.
 * 즉, 올바른 PG사(Toss, Kakao...) 클라이언트를 찾아 작업을 위임하는지 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

	// 테스트 대상(SUT)
	private PaymentFacade paymentFacade;

	// Facade가 주입받을 Mock 클라이언트들
	@Mock
	private PaymentGatewayClient tossClient;
	@Mock
	private PaymentGatewayClient kakaoClient;

	// 테스트용 공통 DTO
	private PaymentParamRequest paramRequest;
	private PaymentCancelRequest cancelRequest;

	@BeforeEach
	void setUp() {
		// --- GIVEN ---
		// 1. 각 Mock 클라이언트가 자신의 Provider 타입을 반환하도록 설정
		given(tossClient.getProviderType()).willReturn(PaymentProvider.TOSS_PAYMENTS);
		given(kakaoClient.getProviderType()).willReturn(PaymentProvider.KAKAO_PAY);

		// 2. Spring의 @Autowired List<PaymentGatewayClient> 동작을 수동으로 모방
		List<PaymentGatewayClient> clients = List.of(tossClient, kakaoClient);

		// 3. 수동으로 SUT(PaymentFacade)를 생성하여 Mock List를 주입
		// (@InjectMocks는 List<T> 타입의 생성자 주입을 직접 지원하지 않으므로 수동 생성)
		paymentFacade = new PaymentFacade(clients);

		// 4. 테스트용 DTO 생성
		paramRequest = new PaymentParamRequest("paymentKey_123", "order_123", new BigDecimal("10000"));
		cancelRequest = new PaymentCancelRequest("cancel_reason_123", "paymentKey_123");
	}

	@Test
	@DisplayName("결제 승인 - TOSS_PAYMENTS 요청 시 TossClient가 호출됨")
	void confirmPayment_WithToss_ShouldCallTossClient() {
		// given
		PaymentProvider provider = PaymentProvider.TOSS_PAYMENTS;
		PaymentConfirmResponse mockResponse = new PaymentConfirmResponse("paymentKey_123",
			"order_123",
			"도서 외 1건",
			"DONE",
			new BigDecimal(10000),
			LocalDateTime.now(),
			LocalDateTime.now(),
			"간편결제");

		// TossClient가 confirm을 호출하면 mockResponse를 반환하도록 설정
		given(tossClient.confirm(paramRequest)).willReturn(mockResponse);

		// when
		PaymentConfirmResponse response = paymentFacade.confirmPayment(provider, paramRequest);

		// then
		assertThat(response).isEqualTo(mockResponse); // 응답이 올바른지 확인
		verify(tossClient, times(1)).confirm(paramRequest); // TossClient가 1번 호출되었는지
		verify(kakaoClient, never()).confirm(any()); // KakaoClient는 호출되지 않았는지
	}

	@Test
	@DisplayName("결제 승인 - KAKAO_PAY 요청 시 KakaoClient가 호출됨")
	void confirmPayment_WithKakao_ShouldCallKakaoClient() {
		// given
		PaymentProvider provider = PaymentProvider.KAKAO_PAY;
		PaymentConfirmResponse mockResponse = new PaymentConfirmResponse("paymentKey_123",
			"order_123",
			"도서 외 1건",
			"DONE",
			new BigDecimal(10000),
			LocalDateTime.now(),
			LocalDateTime.now(),
			"간편결제");
		// KakaoClient가 confirm을 호출하면 mockResponse를 반환하도록 설정
		given(kakaoClient.confirm(paramRequest)).willReturn(mockResponse);

		// when
		PaymentConfirmResponse response = paymentFacade.confirmPayment(provider, paramRequest);

		// then
		assertThat(response).isEqualTo(mockResponse);
		verify(kakaoClient, times(1)).confirm(paramRequest);
		verify(tossClient, never()).confirm(any());
	}

	@Test
	@DisplayName("결제 취소 - TOSS_PAYMENTS 요청 시 TossClient가 호출됨")
	void cancelPayment_WithToss_ShouldCallTossClient() {
		// given
		PaymentProvider provider = PaymentProvider.TOSS_PAYMENTS;
		PaymentCancelResponse mockResponse = new PaymentCancelResponse("order_123", "CANCELED");
		given(tossClient.cancel(cancelRequest)).willReturn(mockResponse);

		// when
		PaymentCancelResponse response = paymentFacade.cancelPayment(provider, cancelRequest);

		// then
		assertThat(response).isEqualTo(mockResponse);
		verify(tossClient, times(1)).cancel(cancelRequest);
		verify(kakaoClient, never()).cancel(any());
	}

	@Test
	@DisplayName("결제 승인 - 지원하지 않는 PG사(NAVER_PAY) 요청 시 예외 발생")
	void confirmPayment_WithUnsupportedProvider_ShouldThrowException() {
		// given
		// NaverClient는 setUp()의 List에 등록되지 않았음
		PaymentProvider provider = PaymentProvider.NAVER_PAY;

		// when & then
		assertThatThrownBy(() -> paymentFacade.confirmPayment(provider, paramRequest))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("지원하지 않는 결제 프로바이더입니다: NAVER_PAY");

		// 어떤 클라이언트도 호출되지 않았는지 검증
		verify(tossClient, never()).confirm(any());
		verify(kakaoClient, never()).confirm(any());
	}

	@Test
	@DisplayName("결제 취소 - 지원하지 않는 PG사 요청 시 예외 발생")
	void cancelPayment_WithUnsupportedProvider_ShouldThrowException() {
		// given
		PaymentProvider provider = PaymentProvider.NAVER_PAY;

		// when & then
		assertThatThrownBy(() -> paymentFacade.cancelPayment(provider, cancelRequest))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("지원하지 않는 결제 프로바이더입니다: NAVER_PAY");
	}
}