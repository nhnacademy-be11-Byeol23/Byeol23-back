package com.nhnacademy.byeol23backend.orderset.payment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentCancelResponse;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentParamRequest;
import com.nhnacademy.byeol23backend.orderset.payment.domain.dto.PaymentResultResponse;
import com.nhnacademy.byeol23backend.orderset.payment.service.PaymentService;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import io.jsonwebtoken.Claims;

@Disabled
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PaymentService paymentService;

	// (필터/인터셉터 의존성 Mocking - Context Load 실패 방지)
	@MockBean
	private JwtParser jwtParser;
	@MockBean
	private MemberRepository memberRepository;

	// 테스트용 DTO 선언
	private PaymentParamRequest paramRequest;
	private PaymentResultResponse resultResponse;
	private PaymentCancelRequest cancelRequest;
	private PaymentCancelResponse cancelResponse;

	@BeforeEach
	void setUp() {
		// (필터/인터셉터 Mocking 설정)
		Claims mockClaims = Mockito.mock(Claims.class);
		given(jwtParser.parseToken(anyString())).willReturn(mockClaims);
		given(mockClaims.get(eq("memberId"), eq(Long.class))).willReturn(1L);
		Member mockMember = Mockito.mock(Member.class);
		given(memberRepository.getReferenceById(anyLong())).willReturn(mockMember);
		given(mockMember.getMemberId()).willReturn(1L);

		// 테스트용 DTO 초기화 (DTO 구조를 추정하여 작성)
		paramRequest = new PaymentParamRequest("paymentKey_test123", "order_test123", new BigDecimal("10000"));

		resultResponse = new PaymentResultResponse(
			"paymentKey_test123",
			"order_test123",
			"주문 상품 외 1건",
			"DONE",
			new BigDecimal("10000"),
			LocalDateTime.now(),
			LocalDateTime.now(),
			"간편 결제"
		);

		cancelRequest = new PaymentCancelRequest("단순 변심", "paymentKey_test123");

		cancelResponse = new PaymentCancelResponse(
			"paymentKey_test123",
			"단순 변심"
		);
	}

	@Test
	@DisplayName("POST /api/payments/confirm (결제 승인)")
	void confirmPayment_Success() throws Exception {
		// given
		given(paymentService.confirmPayment(any(PaymentParamRequest.class))).willReturn(resultResponse);

		// when & then
		mockMvc.perform(post("/api/payments/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paramRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderId").value("order_test123"))
			.andExpect(jsonPath("$.paymentKey").value("paymentKey_test123"))
			.andExpect(jsonPath("$.status").value("DONE"));

		verify(paymentService, times(1)).confirmPayment(any(PaymentParamRequest.class));
	}

	@Test
	@DisplayName("POST /api/payments/cancel (결제 취소)")
	void cancelPayment_Success() throws Exception {
		// given
		given(paymentService.cancelPayment(any(PaymentCancelRequest.class))).willReturn(cancelResponse);

		// when & then
		mockMvc.perform(post("/api/payments/cancel")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cancelRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentKey").value("paymentKey_test123"))
			.andExpect(jsonPath("$.cancelReason").value("단순 변심"));

		verify(paymentService, times(1)).cancelPayment(any(PaymentCancelRequest.class));
	}

	@Test
	@DisplayName("POST /api/payments (결제 정보 생성 - Webhook)")
	void createPayment_Success() throws Exception {
		// given
		// createPayment는 void를 반환하므로 doNothing() 사용
		doNothing().when(paymentService).createPayment(any(PaymentResultResponse.class));

		// when & then
		mockMvc.perform(post("/api/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resultResponse)))
			.andExpect(status().isOk()); // (ResponseEntity<Void>가 없으므로 200 OK)

		verify(paymentService, times(1)).createPayment(any(PaymentResultResponse.class));
	}
}