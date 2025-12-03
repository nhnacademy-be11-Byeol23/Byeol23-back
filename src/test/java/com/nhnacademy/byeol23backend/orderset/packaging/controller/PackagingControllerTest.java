package com.nhnacademy.byeol23backend.orderset.packaging.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateRequest;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingCreateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingInfoResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateRequest;
import com.nhnacademy.byeol23backend.config.SecurityConfig;
import com.nhnacademy.byeol23backend.orderset.packaging.domain.dto.PackagingUpdateResponse;
import com.nhnacademy.byeol23backend.orderset.packaging.service.PackagingService;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import io.jsonwebtoken.Claims;

@Disabled
@WebMvcTest(PackagingController.class)
class PackagingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PackagingService packagingService;

	// (필터/인터셉터 의존성 Mocking - Context Load 실패 방지)
	@MockBean
	private JwtParser jwtParser;
	@MockBean
	private MemberRepository memberRepository;

	private PackagingInfoResponse packagingInfoResponse;
	private PackagingCreateRequest packagingCreateRequest;
	private PackagingCreateResponse packagingCreateResponse;
	private PackagingUpdateRequest packagingUpdateRequest;
	private PackagingUpdateResponse packagingUpdateResponse;

	@BeforeEach
	void setUp() {
		// 테스트용 DTO 샘플 데이터 초기화
		packagingInfoResponse = new PackagingInfoResponse(1L, "테스트 포장지", new BigDecimal("1000"),
			"http://image.url/test.png");
		packagingCreateRequest = new PackagingCreateRequest("새 포장지", new BigDecimal("1500"));
		packagingCreateResponse = new PackagingCreateResponse(2L, "새 포장지", new BigDecimal("1500"),
			"http://image.url/new.png");
		packagingUpdateRequest = new PackagingUpdateRequest("수정된 포장지", new BigDecimal("1200"));
		packagingUpdateResponse = new PackagingUpdateResponse("수정된 포장지", new BigDecimal("1200"),
			"http://image.url/update.png");

		Claims mockClaims = Mockito.mock(Claims.class);
		given(jwtParser.parseToken(any(String.class))).willReturn(mockClaims); // anyString() -> any(String.class)
		given(mockClaims.get(eq("memberId"), eq(Long.class))).willReturn(1L);

		// ▼▼▼ [수정] TokenFilter가 의존하는 MemberRepository의 동작을 정의합니다. ▼▼▼
		// 1. 가짜 Member 객체를 만듭니다.
		Member mockMember = Mockito.mock(Member.class);

		// 2. TokenFilter가 getReferenceById(1L) (토큰이 없을 때 기본 회원 조회)를 호출하면
		//    위에서 만든 가짜 Member 객체를 반환하도록 설정합니다.
		given(memberRepository.getReferenceById(anyLong())).willReturn(mockMember);

		// 3. TokenFilter가 반환된 가짜 Member 객체의 .getMemberId()를 호출할 때
		//    NPE가 발생하지 않도록 1L을 반환하도록 설정합니다.
		given(mockMember.getMemberId()).willReturn(1L);
	}

	@Test
	@DisplayName("GET /api/packagings (포장지 목록 페이징 조회)")
	void getAllPackagings_Success() throws Exception {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<PackagingInfoResponse> responsePage = new PageImpl<>(List.of(packagingInfoResponse), pageable, 1);
		given(packagingService.getAllPacakings(any(Pageable.class))).willReturn(responsePage);

		// when & then
		mockMvc.perform(get("/api/packagings")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].packagingName").value("테스트 포장지"))
			.andExpect(jsonPath("$.totalElements").value(1));

		verify(packagingService, times(1)).getAllPacakings(any(Pageable.class));
	}

	@Test
	@DisplayName("POST /api/packagings (포장지 생성)")
	void createPackaging_Success() throws Exception {
		// given
		given(packagingService.createPackaging(any(PackagingCreateRequest.class))).willReturn(packagingCreateResponse);

		// when & then
		mockMvc.perform(post("/api/packagings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(packagingCreateRequest)))
			.andExpect(status().isCreated()) // 201 Created
			.andExpect(header().string("Location", "/api/packagings/" + packagingCreateResponse.packagingId()))
			.andExpect(jsonPath("$.packagingName").value("새 포장지"));

		verify(packagingService, times(1)).createPackaging(any(PackagingCreateRequest.class));
	}

	@Test
	@DisplayName("PUT /api/packagings/{id} (포장지 수정)")
	void updatePackaging_Success() throws Exception {
		// given
		Long packagingId = 1L;
		given(packagingService.updatePackaging(eq(packagingId), any(PackagingUpdateRequest.class))).willReturn(
			packagingUpdateResponse);

		// when & then
		mockMvc.perform(put("/api/packagings/{packaging-id}", packagingId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(packagingUpdateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.packagingName").value("수정된 포장지"))
			.andExpect(jsonPath("$.packagingPrice").value(1200));

		verify(packagingService, times(1)).updatePackaging(eq(packagingId), any(PackagingUpdateRequest.class));
	}

	@Test
	@DisplayName("DELETE /api/packagings/{id} (포장지 삭제)")
	void deleteById_Success() throws Exception {
		// given
		Long packagingId = 1L;
		doNothing().when(packagingService).deletePackagingById(packagingId);

		// when & then
		mockMvc.perform(delete("/api/packagings/{packaging-id}", packagingId))
			.andExpect(status().isNoContent()); // 204 No Content

		verify(packagingService, times(1)).deletePackagingById(packagingId);
	}

	@Test
	@DisplayName("GET /api/packagings/lists (포장지 전체 목록 조회 - 리스트)")
	void getAllPackagingLists_Success() throws Exception {
		// given
		List<PackagingInfoResponse> responseList = List.of(packagingInfoResponse);
		given(packagingService.getPackagingLists()).willReturn(responseList);

		// when & then
		mockMvc.perform(get("/api/packagings/lists"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].packagingName").value("테스트 포장지"));

		verify(packagingService, times(1)).getPackagingLists();
	}
}