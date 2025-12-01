package com.nhnacademy.byeol23backend.memberset.member.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23backend.memberset.member.dto.FindLoginIdResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.ValueDuplicationCheckRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.ValueDuplicationCheckResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateResponse;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;
import com.nhnacademy.byeol23backend.utils.MemberUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController implements MemberApi {
	private final MemberService memberService;

	/**
	 * 회원가입 요청 <br>
	 * @param request MemberCreateRequest
	 * @return 201(CREATED)
	 */
	@PostMapping
	public ResponseEntity<MemberCreateResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
		MemberCreateResponse createdMember = memberService.createMember(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(createdMember);
	}

	@GetMapping("/check-id")
	public ResponseEntity<FindLoginIdResponse> checkId(@RequestParam String loginId) {
		boolean isDuplicated = memberService.checkIdDuplicated(loginId);
		return ResponseEntity.ok(new FindLoginIdResponse(isDuplicated));
	}

	@PostMapping("/check-duplication")
	public ResponseEntity<ValueDuplicationCheckResponse> checkDuplication(@RequestBody ValueDuplicationCheckRequest request) {
		ValueDuplicationCheckResponse response = memberService.checkDuplication(request);

		return ResponseEntity.ok(response);
	}

	/**
	 * 마이 페이지 요청
	 *
	 * @param
	 * @return 200(OK) / MemberMyPageResponse
	 */
	@GetMapping
	public ResponseEntity<MemberMyPageResponse> getMember() {
		Long memberId = MemberUtil.getMemberId();
		return ResponseEntity.ok(memberService.getMember(memberId));
	}

	/**
	 * 회원 정보 수정 요청
	 * @param request MemberUpdateRequest
	 * @return 200(OK) / MemberUpdateResponse
	 */
	@PutMapping
	public ResponseEntity<MemberUpdateResponse> updateMember(
		@Valid @RequestBody MemberUpdateRequest request
	) {
		Long memberId = MemberUtil.getMemberId();
		return ResponseEntity.ok(memberService.updateMember(memberId, request));
	}

	@PutMapping("/password")
	public ResponseEntity<MemberPasswordUpdateResponse> updateMemberPassword(
		@Valid @RequestBody MemberPasswordUpdateRequest request
	) {
		Long memberId = MemberUtil.getMemberId();
		return ResponseEntity.ok(memberService.updateMemberPassword(memberId, request));
	}

	@PutMapping("/reactivate")
	public ResponseEntity<Void> reactivateMember(
		@Valid @RequestBody MemberPasswordUpdateRequest request
	) {
		Long memberId = MemberUtil.getMemberId();
		memberService.reactivateMember(memberId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 회원 삭제에 관한 요청을 처리한다.
	 * @return 204(NO CONTENT)
	 */
	@DeleteMapping
	public ResponseEntity<Void> deleteMember(
	) {
		Long memberId = MemberUtil.getMemberId();
		memberService.deleteMember(memberId);
		return ResponseEntity.noContent().build();
	}
}

