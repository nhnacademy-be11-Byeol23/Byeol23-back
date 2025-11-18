package com.nhnacademy.byeol23backend.memberset.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberCreateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberMyPageResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberPasswordUpdateResponse;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateRequest;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberUpdateResponse;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController implements MemberApi {
	private final MemberService memberService;

	/**
	 * 회원가입 요청 <br>
	 * (현재는 아무 것도 반환하지 않는다)
	 * @param request MemberCreateRequest
	 * @return 201(CREATED)
	 */
	@PostMapping("/register")
	public ResponseEntity<MemberCreateResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
		MemberCreateResponse createdMember = memberService.createMember(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(createdMember);
	}

	/**
	 * 마이 페이지 요청
	 * @param token String
	 * @return 200(OK) / MemberMyPageResponse
	 */
	@GetMapping
	public ResponseEntity<MemberMyPageResponse> getMember(@CookieValue(name = "Access-Token") String token) {
		return ResponseEntity.ok(memberService.getMember(token));
	}

	/**
	 * 회원 정보 수정 요청
	 * @param memberId Long
	 * @param request MemberUpdateRequest
	 * @return 200(OK) / MemberUpdateResponse
	 */
	@PutMapping("/{member-id}")
	public ResponseEntity<MemberUpdateResponse> updateMember(
		@PathVariable(value = "member-id") Long memberId,
		@Valid @RequestBody MemberUpdateRequest request
	) {
		return ResponseEntity.ok(memberService.updateMember(memberId, request));
	}

	@PutMapping("/{member-id}/password")
	public ResponseEntity<MemberPasswordUpdateResponse> updateMemberPassword(
		@PathVariable(value = "member-id") Long memberId,
		@Valid @RequestBody MemberPasswordUpdateRequest request
	) {
		return ResponseEntity.ok(memberService.updateMemberPassword(memberId, request));
	}

	@PutMapping("/{member-id}/reactivate")
	public ResponseEntity<Void> reactivateMember(
		@PathVariable(value = "member-id") Long memberId,
		@Valid @RequestBody MemberPasswordUpdateRequest request
	) {
		memberService.reactivateMember(memberId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 회원 삭제에 관한 요청을 처리한다.
	 * @param memberId Long
	 * @return 204(NO CONTENT)
	 */
	@DeleteMapping("/{member-id}")
	public ResponseEntity<Void> deleteMember(@PathVariable(value = "member-id") Long memberId) {
		memberService.deleteMember(memberId);
		return ResponseEntity.noContent().build();
	}

}

