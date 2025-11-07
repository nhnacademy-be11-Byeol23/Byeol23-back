package com.nhnacademy.byeol23backend.memberset.member.controller;

import com.nhnacademy.byeol23backend.memberset.member.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController implements MemberApi{
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
	 * @param memberId Long
	 * @return 200(OK) / MemberMyPageResponse
	 */
	@GetMapping("/{member-id}")
	public ResponseEntity<MemberMyPageResponse> getMember(@PathVariable(value = "member-id") Long memberId) {
		return ResponseEntity.ok(memberService.getMember(memberId));
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
			@Valid @RequestBody  MemberPasswordUpdateRequest request
	) {
		return ResponseEntity.ok(memberService.updateMemberPassword(memberId, request));
	}

	@PutMapping("/{member-id}/reactivate")
	public ResponseEntity<Void> reactivateMember(
			@PathVariable(value = "member-id") Long memberId,
			@Valid @RequestBody  MemberPasswordUpdateRequest request
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

