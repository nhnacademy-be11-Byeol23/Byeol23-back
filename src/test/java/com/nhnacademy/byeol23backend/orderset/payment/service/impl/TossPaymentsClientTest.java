package com.nhnacademy.byeol23backend.orderset.payment.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.byeol23backend.orderset.payment.domain.PaymentProvider;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentConfirmResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentCancelException;
import com.nhnacademy.byeol23backend.orderset.payment.exception.PaymentConfirmException;
import com.nhnacademy.byeol23backend.orderset.payment.exception.TossApiRequestException;

@ExtendWith(MockitoExtension.class)
class TossPaymentsClientTest {

	// 테스트 대상 (SUT)
	private TossPaymentsClient tossPaymentsClient;

	// ObjectMapper는 실제 객체를 사용 (JSON 직렬화/역직렬화 테스트)
	private ObjectMapper objectMapper;

	@Mock
	private HttpClient mockHttpClient;
	@Mock
	private HttpResponse<String> mockHttpResponse;

	private PaymentParamRequest confirmParamRequest;
	private PaymentCancelRequest cancelParamRequest;
	private String secretKey = "test_sk_1234567890abcdefg"; // 테스트용 시크릿 키

	@BeforeEach
	void setUp() {
		// OffsetDateTime 파싱을 위해 JavaTimeModule 등록
		objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

		// SUT 수동 생성 및 의존성 주입
		tossPaymentsClient = new TossPaymentsClient(objectMapper);
		// @Value 필드 수동 주입
		ReflectionTestUtils.setField(tossPaymentsClient, "secretKey", secretKey);

		// 테스트용 DTO 생성
		confirmParamRequest = new PaymentParamRequest("paymentKey_123", "order_123", new BigDecimal("10000"));
		cancelParamRequest = new PaymentCancelRequest("단순 변심", "paymentKey_123");
	}

	@Test
	@DisplayName("getProviderType()은 TOSS_PAYMENTS를 반환한다")
	void getProviderType_Success() {
		// when
		PaymentProvider provider = tossPaymentsClient.getProviderType();
		// then
		assertThat(provider).isEqualTo(PaymentProvider.TOSS_PAYMENTS);
	}

	@Test
	@DisplayName("결제 승인(confirm) 성공")
	void confirm_Success() throws IOException, InterruptedException {
		// given
		// Toss API가 반환할 가짜 JSON 응답
		String fakeTossResponse = """
			{
			    "paymentKey": "paymentKey_123",
			    "orderId": "order_123",
			    "orderName": "테스트 주문",
			    "status": "DONE",
			    "totalAmount": 10000,
			    "requestedAt": "2025-11-15T10:00:00+09:00",
			    "approvedAt": "2025-11-15T10:01:00+09:00",
			    "method": "카드"
			}
			""";

		// HTTP 200 OK 및 가짜 JSON 응답 설정
		given(mockHttpResponse.statusCode()).willReturn(200);
		given(mockHttpResponse.body()).willReturn(fakeTossResponse);

		// HttpClient.newHttpClient().send(...) 모킹
		// try-with-resources 구문으로 static 메서드 모킹
		try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
			mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
			given(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.willReturn(mockHttpResponse);

			// when
			PaymentConfirmResponse response = tossPaymentsClient.confirm(confirmParamRequest);

			// then
			assertThat(response).isNotNull();
			assertThat(response.orderId()).isEqualTo("order_123");
			assertThat(response.status()).isEqualTo("DONE");
			assertThat(response.totalAmount()).isEqualByComparingTo("10000");
			assertThat(response.method()).isEqualTo("카드");

			// HttpClient.send가 1번 호출되었는지 검증
			verify(mockHttpClient, times(1)).send(any(), any());
		}
	}

	@Test
	@DisplayName("결제 승인(confirm) 실패 - Toss API가 4xx 반환")
	void confirm_ApiFails_ThrowsPaymentConfirmException() throws IOException, InterruptedException {
		// given
		String fakeErrorResponse = "{\"code\":\"INVALID_REQUEST\",\"message\":\"잘못된 요청\"}";

		// HTTP 400 Bad Request 설정
		given(mockHttpResponse.statusCode()).willReturn(400);
		given(mockHttpResponse.body()).willReturn(fakeErrorResponse);

		// HttpClient.newHttpClient().send(...) 모킹
		try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
			mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
			given(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.willReturn(mockHttpResponse);

			// when & then
			assertThatThrownBy(() -> tossPaymentsClient.confirm(confirmParamRequest))
				.isInstanceOf(PaymentConfirmException.class)
				.hasMessageContaining("Toss API 결제 승인에 실패했습니다.");
		}
	}

	@Test
	@DisplayName("결제 승인(confirm) 실패 - 네트워크 (IOException)")
	void confirm_NetworkFails_ThrowsTossApiRequestException() throws IOException, InterruptedException {
		// given
		// HttpClient.send()가 IOException을 던지도록 설정
		try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
			mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
			given(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.willThrow(new IOException("네트워크 연결 실패"));

			// when & then
			assertThatThrownBy(() -> tossPaymentsClient.confirm(confirmParamRequest))
				.isInstanceOf(TossApiRequestException.class)
				.hasMessageContaining("Toss Api 요청에 실패했습니다.");
		}
	}

	@Test
	@DisplayName("결제 취소(cancel) 성공")
	void cancel_Success() throws IOException, InterruptedException {
		// given
		String fakeTossResponse = """
			{
			    "paymentKey": "paymentKey_123",
			    "cancelReason": "단순 변심"
			}
			""";
		given(mockHttpResponse.statusCode()).willReturn(200);
		given(mockHttpResponse.body()).willReturn(fakeTossResponse);

		try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
			mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
			given(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.willReturn(mockHttpResponse);

			// when
			PaymentCancelResponse response = tossPaymentsClient.cancel(cancelParamRequest);

			// then
			assertThat(response).isNotNull();
			assertThat(response.paymentKey()).isEqualTo("paymentKey_123");
			assertThat(response.cancelReason()).isEqualTo("단순 변심");

			verify(mockHttpClient, times(1)).send(any(), any());
		}
	}

	@Test
	@DisplayName("결제 취소(cancel) 실패 - Toss API가 4xx 반환")
	void cancel_ApiFails_ThrowsPaymentCancelException() throws IOException, InterruptedException {
		// given
		given(mockHttpResponse.statusCode()).willReturn(400);

		try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
			mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
			given(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
				.willReturn(mockHttpResponse);

			// when & then
			assertThatThrownBy(() -> tossPaymentsClient.cancel(cancelParamRequest))
				.isInstanceOf(PaymentCancelException.class)
				.hasMessageContaining("Toss Payments Api 결제 취소에 실패했습니다.");
		}
	}
}