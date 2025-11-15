package com.nhnacademy.byeol23backend.orderset.refund.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
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
import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundRequest;
import com.nhnacademy.byeol23backend.orderset.refund.domain.dto.RefundResponse;
import com.nhnacademy.byeol23backend.orderset.refund.service.RefundService;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundOption;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import io.jsonwebtoken.Claims;

@WebMvcTest(RefundController.class)
class RefundControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RefundService refundService;

	// (필터/인터셉터 의존성 Mocking - Context Load 실패 방지)
	@MockBean
	private JwtParser jwtParser;
	@MockBean
	private MemberRepository memberRepository;

	// 테스트용 DTO
	private RefundRequest refundRequest;
	private RefundResponse refundResponse;

	@BeforeEach
	void setUp() {
		// (필터/인터셉터 Mocking 설정)
		Claims mockClaims = Mockito.mock(Claims.class);
		given(jwtParser.parseToken(anyString())).willReturn(mockClaims);
		given(mockClaims.get(eq("memberId"), eq(Long.class))).willReturn(1L);
		Member mockMember = Mockito.mock(Member.class);
		given(memberRepository.getReferenceById(anyLong())).willReturn(mockMember);
		given(mockMember.getMemberId()).willReturn(1L);

		// 테스트용 DTO 초기화
		// (RefundRequest, RefundResponse DTO 구조를 JavaDoc 기반으로 추정)
		refundRequest = new RefundRequest(
			"order-12345",
			"MIND_CHANGED", // RefundReason (Enum 또는 String)
			RefundOption.MIND_CHANGED  // RefundOption (Enum 또는 String)
		);

		refundResponse = new RefundResponse(
			"order-12345",
			"MIND_CHANGED",
			RefundOption.MIND_CHANGED,
			new BigDecimal("15000"),
			LocalDateTime.now()
		);
	}

	@Test
	@DisplayName("POST /api/refunds (환불 요청)")
	void refundRequest_Success() throws Exception {
		// given
		// RefundService가 refundRequest를 호출받으면, 미리 정의된 refundResponse를 반환하도록 설정
		given(refundService.refundRequest(any(RefundRequest.class))).willReturn(refundResponse);

		// when & then
		mockMvc.perform(post("/api/refunds")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refundRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.orderNumber").value(refundResponse.orderNumber()))
			.andExpect(jsonPath("$.refundReason").value(refundResponse.refundReason()))
			.andExpect(
				jsonPath("$.refundPrice").value(refundResponse.refundPrice().intValue())); // BigDecimal을 intValue로 비교

		// verify
		// refundService.refundRequest가 1번 호출되었는지 검증
		verify(refundService, times(1)).refundRequest(any(RefundRequest.class));
	}
}