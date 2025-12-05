package com.nhnacademy.byeol23backend.memberset.member.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;
import com.nhnacademy.byeol23backend.memberset.grade.dto.AllGradeResponse;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;
import com.nhnacademy.byeol23backend.memberset.member.dto.*;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.utils.MemberUtil;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

	@Mock
	private MemberService memberService;

	@InjectMocks
	private MemberController memberController;

	private Long testMemberId;

	@BeforeEach
	void setUp() {
		testMemberId = 1L;
	}

	@Test
	@DisplayName("회원가입 성공 - POST /api/members")
	void createMember_Success() {
		// given
		MemberCreateRequest request = new MemberCreateRequest(
			"testuser",
			"password123",
			"홍길동",
			"길동이",
			"01012345678",
			"test@example.com",
			LocalDate.of(1990, 1, 1),
			Role.USER,
			com.nhnacademy.byeol23backend.memberset.member.domain.RegistrationSource.WEB
		);

		MemberCreateResponse response = new MemberCreateResponse();
		given(memberService.createMember(any(MemberCreateRequest.class))).willReturn(response);

		// when
		ResponseEntity<MemberCreateResponse> result = memberController.createMember(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(result.getBody()).isNotNull();
		verify(memberService, times(1)).createMember(request);
	}

	@Test
	@DisplayName("ID 중복 확인 성공 - GET /api/members/check-id")
	void checkId_Success() {
		// given
		String loginId = "testuser";
		boolean isDuplicated = false;
		given(memberService.checkIdDuplicated(loginId)).willReturn(isDuplicated);

		// when
		ResponseEntity<CheckIdResponse> result = memberController.checkId(loginId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().isDuplicated()).isFalse();
		verify(memberService, times(1)).checkIdDuplicated(loginId);
	}

	@Test
	@DisplayName("ID 중복 확인 - 중복된 ID")
	void checkId_Duplicated() {
		// given
		String loginId = "testuser";
		boolean isDuplicated = true;
		given(memberService.checkIdDuplicated(loginId)).willReturn(isDuplicated);

		// when
		ResponseEntity<CheckIdResponse> result = memberController.checkId(loginId);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().isDuplicated()).isTrue();
		verify(memberService, times(1)).checkIdDuplicated(loginId);
	}

	@Test
	@DisplayName("마이페이지 조회 성공 - GET /api/members")
	void getMember_Success() {
		// given
		MemberMyPageResponse response = new MemberMyPageResponse(
			"testuser",
			"홍길동",
			"길동이",
			"01012345678",
			"test@example.com",
			LocalDate.of(1990, 1, 1),
			new BigDecimal("10000"),
			Role.USER,
			"일반",
			new AddressResponse(1L, "04410", "서울시 강남구", "테헤란로 123", "상세주소", "집", true),
			List.of(new AllGradeResponse("일반", new BigDecimal("0"), new BigDecimal("0.01")))
		);

		try (MockedStatic<MemberUtil> memberUtilMock = mockStatic(MemberUtil.class)) {
			memberUtilMock.when(MemberUtil::getMemberId).thenReturn(testMemberId);
			given(memberService.getMember(testMemberId)).willReturn(response);

			// when
			ResponseEntity<MemberMyPageResponse> result = memberController.getMember();

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody()).isNotNull();
			assertThat(result.getBody().loginId()).isEqualTo("testuser");
			assertThat(result.getBody().memberName()).isEqualTo("홍길동");
			assertThat(result.getBody().nickname()).isEqualTo("길동이");
			assertThat(result.getBody().currentPoint()).isEqualByComparingTo(new BigDecimal("10000"));
			verify(memberService, times(1)).getMember(testMemberId);
		}
	}

	@Test
	@DisplayName("마이페이지 조회 실패 - 존재하지 않는 회원")
	void getMember_Fail_NotFound() {
		// given
		try (MockedStatic<MemberUtil> memberUtilMock = mockStatic(MemberUtil.class)) {
			memberUtilMock.when(MemberUtil::getMemberId).thenReturn(testMemberId);
			given(memberService.getMember(testMemberId))
				.willThrow(new MemberNotFoundException("회원을 찾을 수 없습니다."));

			// when & then
			assertThatThrownBy(() -> memberController.getMember())
				.isInstanceOf(MemberNotFoundException.class)
				.hasMessageContaining("회원을 찾을 수 없습니다");

			verify(memberService, times(1)).getMember(testMemberId);
		}
	}

	@Test
	@DisplayName("회원 정보 수정 성공 - POST /api/members/put")
	void updateMember_Success() {
		// given
		MemberUpdateRequest request = new MemberUpdateRequest(
			"홍길동",
			"길동이2",
			"01087654321",
			"newemail@example.com",
			LocalDate.of(1990, 1, 1)
		);

		MemberUpdateResponse response = new MemberUpdateResponse();
		try (MockedStatic<MemberUtil> memberUtilMock = mockStatic(MemberUtil.class)) {
			memberUtilMock.when(MemberUtil::getMemberId).thenReturn(testMemberId);
			given(memberService.updateMember(eq(testMemberId), any(MemberUpdateRequest.class)))
				.willReturn(response);

			// when
			ResponseEntity<MemberUpdateResponse> result = memberController.updateMember(request);

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody()).isNotNull();
			verify(memberService, times(1)).updateMember(testMemberId, request);
		}
	}

	@Test
	@DisplayName("비밀번호 변경 성공 - POST /api/members/put/password")
	void updateMemberPassword_Success() {
		// given
		MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest(
			"testuser",
			"oldPassword",
			"newPassword"
		);

		MemberPasswordUpdateResponse response = new MemberPasswordUpdateResponse();
		try (MockedStatic<MemberUtil> memberUtilMock = mockStatic(MemberUtil.class)) {
			memberUtilMock.when(MemberUtil::getMemberId).thenReturn(testMemberId);
			given(memberService.updateMemberPassword(eq(testMemberId), any(MemberPasswordUpdateRequest.class)))
				.willReturn(response);

			// when
			ResponseEntity<MemberPasswordUpdateResponse> result = memberController.updateMemberPassword(request);

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody()).isNotNull();
			verify(memberService, times(1)).updateMemberPassword(testMemberId, request);
		}
	}

	@Test
	@DisplayName("회원 재활성화 성공 - PUT /api/members/reactivate")
	void reactivateMember_Success() {
		// given
		MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest(
			"testuser",
			"password",
			"newPassword"
		);

		try (MockedStatic<MemberUtil> memberUtilMock = mockStatic(MemberUtil.class)) {
			memberUtilMock.when(MemberUtil::getMemberId).thenReturn(testMemberId);
			willDoNothing().given(memberService).reactivateMember(testMemberId);

			// when
			ResponseEntity<Void> result = memberController.reactivateMember(request);

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			assertThat(result.getBody()).isNull();
			verify(memberService, times(1)).reactivateMember(testMemberId);
		}
	}

	@Test
	@DisplayName("회원 탈퇴 성공 - POST /api/members/delete")
	void deleteMember_Success() {
		// given
		try (MockedStatic<MemberUtil> memberUtilMock = mockStatic(MemberUtil.class)) {
			memberUtilMock.when(MemberUtil::getMemberId).thenReturn(testMemberId);
			willDoNothing().given(memberService).deleteMember(testMemberId);

			// when
			ResponseEntity<Void> result = memberController.deleteMember();

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
			assertThat(result.getBody()).isNull();
			verify(memberService, times(1)).deleteMember(testMemberId);
		}
	}

	@Test
	@DisplayName("회원 정보 중복 확인 성공 - GET /api/members/check-duplication")
	void checkDuplication_Success() {
		// given
		ValueDuplicatedRequest request = new ValueDuplicatedRequest(
			"길동이",
			"01012345678",
			"test@example.com"
		);

		ValueDuplicatedResponse response = new ValueDuplicatedResponse(
			false,
			false,
			false
		);

		given(memberService.checkInfoDuplicated(any(ValueDuplicatedRequest.class))).willReturn(response);

		// when
		ResponseEntity<ValueDuplicatedResponse> result = memberController.checkDuplication(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().isDuplicatedNickname()).isFalse();
		assertThat(result.getBody().isDuplicatedEmail()).isFalse();
		assertThat(result.getBody().isDuplicatedPhoneNumber()).isFalse();
		verify(memberService, times(1)).checkInfoDuplicated(request);
	}

	@Test
	@DisplayName("회원 정보 중복 확인 - 일부 중복")
	void checkDuplication_PartialDuplicated() {
		// given
		ValueDuplicatedRequest request = new ValueDuplicatedRequest(
			"길동이",
			"01012345678",
			"test@example.com"
		);

		ValueDuplicatedResponse response = new ValueDuplicatedResponse(
			false,
			true,
			false
		);

		given(memberService.checkInfoDuplicated(any(ValueDuplicatedRequest.class))).willReturn(response);

		// when
		ResponseEntity<ValueDuplicatedResponse> result = memberController.checkDuplication(request);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody().isDuplicatedNickname()).isFalse();
		assertThat(result.getBody().isDuplicatedEmail()).isTrue();
		assertThat(result.getBody().isDuplicatedPhoneNumber()).isFalse();
		verify(memberService, times(1)).checkInfoDuplicated(request);
	}
}

