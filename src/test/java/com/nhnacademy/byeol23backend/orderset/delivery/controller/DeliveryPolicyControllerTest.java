package com.nhnacademy.byeol23backend.orderset.delivery.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.utils.JwtParser;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.domain.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.delivery.service.DeliveryPolicyService;

@WebMvcTest(DeliveryPolicyController.class)
class DeliveryPolicyControllerTest {

	@Autowired
	private MockMvc mockMvc; // HTTP 요청을 시뮬레이션

	@Autowired
	private ObjectMapper objectMapper; // Java 객체 <-> JSON 변환

	@MockBean
	private DeliveryPolicyService deliveryPolicyService; // 가짜 서비스 레이어

	@MockBean
	private JwtParser jwtParser;

	// 테스트용 공통 DTO
	private DeliveryPolicyInfoResponse infoResponse;
	private DeliveryPolicyCreateRequest createRequest;
	private DeliveryPolicyCreateResponse createResponse;

	@BeforeEach
	void setUp() {
		// DTO 정의는 실제 DTO 구조에 맞게 조정해야 합니다.
		infoResponse = new DeliveryPolicyInfoResponse(
			BigDecimal.valueOf(50000),
			BigDecimal.valueOf(3000),
			LocalDateTime.now()
		);

		createRequest = new DeliveryPolicyCreateRequest(
			BigDecimal.valueOf(60000),
			BigDecimal.valueOf(2500)
		);

		createResponse = new DeliveryPolicyCreateResponse(1L, BigDecimal.valueOf(60000), BigDecimal.valueOf(2500),
			LocalDateTime.now()); // ID가 1L이라고 가정
	}

	@Test
	@DisplayName("배송 정책 목록 페이징 조회 (GET /api/delivery-policies)")
	void getDeliveryPolicies() throws Exception {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<DeliveryPolicyInfoResponse> responsePage = new PageImpl<>(List.of(infoResponse), pageable, 1);

		// Service가 Pageable 객체를 받아 Page 객체를 반환하도록 설정
		given(deliveryPolicyService.getDeliveryPolicies(any(Pageable.class))).willReturn(responsePage);

		// when & then
		mockMvc.perform(get("/api/delivery-policies")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.content[0].deliveryFee").value(3000));
	}

	@Test
	@DisplayName("새 배송 정책 생성 (POST /api/delivery-policies)")
	void createDeliveryPolicy() throws Exception {
		// given
		// Service가 CreateRequest DTO를 받아 CreateResponse DTO를 반환하도록 설정
		given(deliveryPolicyService.createDeliveryPolicy(any(DeliveryPolicyCreateRequest.class)))
			.willReturn(createResponse);

		// when & then
		mockMvc.perform(post("/api/delivery-policies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest))) // 1. 요청 본문(Body)에 JSON 전송
			.andExpect(status().isCreated()) // 2. HTTP 201 Created 상태 검증
			.andExpect(header().string("Location", "/api/delivery-policies/1")) // 3. Location 헤더 검증
			.andExpect(jsonPath("$.deliveryPolicyId").value(1L)); // 4. 응답 본문(JSON) 검증
	}

	@Test
	@DisplayName("현재 적용 중인 배송 정책 조회 (GET /api/delivery-policies/current)")
	void getCurrentDeliveryPolicy() throws Exception {
		// given
		// Service가 infoResponse DTO를 반환하도록 설정
		given(deliveryPolicyService.getCurrentDeliveryPolicy()).willReturn(infoResponse);

		// when & then
		mockMvc.perform(get("/api/delivery-policies/current"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.deliveryFee").value(3000))
			.andExpect(jsonPath("$.freeDeliveryCondition").value(50000));
	}
}