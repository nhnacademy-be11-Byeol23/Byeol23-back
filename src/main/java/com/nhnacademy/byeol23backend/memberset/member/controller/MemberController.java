package com.nhnacademy.byeol23backend.memberset.member.controller;

import com.nhnacademy.byeol23backend.commons.exception.ErrorResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.*;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.utils.MemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
	private final MemberService memberService;

	@PostMapping
	@Operation(summary = "회원 가입", description = "신규 회원 정보를 받아 회원을 생성합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "회원 생성 성공",
					content = @Content(schema = @Schema(implementation = MemberCreateResponse.class))),
			@ApiResponse(responseCode = "400", description = "검증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberCreateResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
		MemberCreateResponse createdMember = memberService.createMember(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(createdMember);
	}

	@GetMapping("/check-id")
	@Operation(summary = "아이디 중복 체크", description = "로그인 아이디가 이미 사용 중인지 확인합니다.")
	public ResponseEntity<CheckIdResponse> checkId(@RequestParam String loginId) {
		boolean isDuplicated = memberService.checkIdDuplicated(loginId);
		return ResponseEntity.ok(new CheckIdResponse(isDuplicated));
	}

	@GetMapping
	@Operation(summary = "회원 마이페이지 조회", description = "현재 인증된 회원의 마이페이지 정보를 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "조회 성공",
					content = @Content(schema = @Schema(implementation = MemberMyPageResponse.class))),
			@ApiResponse(responseCode = "401", description = "인증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberMyPageResponse> getMember() {
		Long memberId = MemberUtil.getMemberId();
		return ResponseEntity.ok(memberService.getMember(memberId));
	}

	@PostMapping("/put")
	@Operation(summary = "회원 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "수정 성공",
					content = @Content(schema = @Schema(implementation = MemberUpdateResponse.class))),
			@ApiResponse(responseCode = "400", description = "검증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "인증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberUpdateResponse> updateMember(
			@Valid @RequestBody MemberUpdateRequest request
	) {
		Long memberId = MemberUtil.getMemberId();
		return ResponseEntity.ok(memberService.updateMember(memberId, request));
	}

	@PostMapping("/put/password")
	@Operation(summary = "비밀번호 변경", description = "현재 로그인한 회원의 비밀번호를 변경합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "변경 성공",
					content = @Content(schema = @Schema(implementation = MemberPasswordUpdateResponse.class))),
			@ApiResponse(responseCode = "400", description = "검증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "인증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberPasswordUpdateResponse> updateMemberPassword(
			@Valid @RequestBody MemberPasswordUpdateRequest request
	) {

		Long memberId = MemberUtil.getMemberId();
		return ResponseEntity.ok(memberService.updateMemberPassword(memberId, request));
	}

	@PutMapping("/reactivate")
	@Operation(summary = "휴면 계정 복구", description = "휴면 상태의 회원 계정을 다시 활성화합니다.")
	public ResponseEntity<Void> reactivateMember(
			@Valid @RequestBody MemberPasswordUpdateRequest request
	) {

		Long memberId = MemberUtil.getMemberId();
		memberService.reactivateMember(memberId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/delete")
	@Operation(summary = "회원 탈퇴", description = "현재 로그인한 회원을 탈퇴 처리합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "탈퇴 성공"),
			@ApiResponse(responseCode = "401", description = "인증 실패",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Void> deleteMember(
	) {
		Long memberId = MemberUtil.getMemberId();
		memberService.deleteMember(memberId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/check-duplication")
	@Operation(summary = "회원 정보 중복 체크", description = "닉네임/이메일/전화번호 등의 중복 여부를 검증합니다.")
	public ResponseEntity<ValueDuplicatedResponse> checkDuplication(@RequestBody ValueDuplicatedRequest request) {
		ValueDuplicatedResponse response = memberService.checkInfoDuplicated(request);
		return ResponseEntity.ok(response);
	}


}

