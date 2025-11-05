package com.nhnacademy.byeol23backend.orderset.refundpolicy.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23backend.bookset.book.utils.JwtParser;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateRequest;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyCreateResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto.RefundPolicyInfoResponse;
import com.nhnacademy.byeol23backend.orderset.refundpolicy.service.RefundPolicyService;

@WebMvcTest(RefundPolicyController.class)
class RefundPolicyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RefundPolicyService refundPolicyService;

	// (이전 오류 방지를 위해 Interceptor가 의존하는 JwtParser도 Mocking)
	@MockBean
	private JwtParser jwtParser;

	// 테스트용 DTO 샘플
	private RefundPolicyInfoResponse infoResponse;
	private RefundPolicyCreateRequest createRequest;
	private RefundPolicyCreateResponse createResponse;

	@BeforeEach
	void setUp() {
		// (DTO 구조는 제공해주신 RefundPolicyInfoResponse를 기반으로 추정)
		infoResponse = new RefundPolicyInfoResponse(
			"기본 환불 정책",
			"7일 이내, 포장 개봉 전",
			"2025년 11월 개정",
			LocalDateTime.now()
		);

		createRequest = new RefundPolicyCreateRequest(
			"신규 환불 정책",
			"14일 이내",
			"신규 생성"
		);

		createResponse = new RefundPolicyCreateResponse(1L, createRequest.refundPolicyName(),
			createRequest.refundCondition(), createRequest.comment(), LocalDateTime.now()); // 새 ID가 1L로 생성되었다고 가정
	}

	@Test
	@DisplayName("GET /api/refund-policies - 환불 정책 목록 페이징 조회 (changedAt 내림차순)")
	void getAllRefundPolicies() throws Exception {
		// given
		// 컨트롤러의 @PageableDefault(sort = "changedAt", direction = DESC)를 반영
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "changedAt"));
		Page<RefundPolicyInfoResponse> responsePage = new PageImpl<>(List.of(infoResponse), pageable, 1);

		// Service가 Pageable 객체를 받아 Page 객체를 반환하도록 설정
		given(refundPolicyService.getAllRefundPolicies(pageable)).willReturn(responsePage);

		// when & then
		mockMvc.perform(get("/api/refund-policies")
				.param("page", "0")
				.param("size", "10")
				// sort 파라미터를 명시적으로 테스트 (컨트롤러 기본값과 일치)
				.param("sort", "changedAt,DESC"))
			.andExpect(status().isOk())
			// (이전 오류 경험을 바탕으로 contentTypeCompatibleWith 사용)
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.content[0].refundPolicyName").value("기본 환불 정책"));
	}

	@Test
	@DisplayName("POST /api/refund-policies - 새 환불 정책 생성")
	void createRefundPolicy() throws Exception {
		// given
		// Service가 CreateRequest DTO를 받아 CreateResponse DTO를 반환하도록 설정
		given(refundPolicyService.createRefundPolicy(any(RefundPolicyCreateRequest.class)))
			.willReturn(createResponse);

		// when & then
		mockMvc.perform(post("/api/refund-policies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest))) // 1. 요청 본문(Body)에 JSON 전송
			.andExpect(status().isCreated()) // 2. HTTP 201 Created 상태 검증
			// 3. 컨트롤러가 생성한 Location 헤더 검증 (맨 앞에 '/'가 없는 상대 경로)
			.andExpect(header().string("Location", "api/refund-policies/1"))
			.andExpect(jsonPath("$.refundPolicyId").value(1L)); // 4. 응답 본문(JSON) 검증
	}

	@Test
	@DisplayName("GET /api/refund-policies/current - 현재 환불 정책 조회")
	void getCurrentRefundPolicy() throws Exception {
		// given
		// Service가 CreateRequest DTO를 받아 CreateResponse DTO를 반환하도록 설정
		given(refundPolicyService.getCurrentRefundPolicy())
			.willReturn(infoResponse);

		// when & then
		mockMvc.perform(get("/api/refund-policies/current"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.refundPolicyName").value("기본 환불 정책"))
			.andExpect(jsonPath("$.refundCondition").value("7일 이내, 포장 개봉 전"));
	}
}