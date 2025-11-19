package com.nhnacademy.byeol23backend.memberset.member.controller;

import com.nhnacademy.byeol23backend.commons.aop.RequireRole;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;
import com.nhnacademy.byeol23backend.memberset.member.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import com.nhnacademy.byeol23backend.orderset.order.domain.dto.OrderPrepareRequest;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController implements MemberApi {
	private final MemberService memberService;
	private final JwtParser jwtParser;

	/**
	 * 회원가입 요청 <br>
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
	 *
	 * @param request
	 * @param accessToken
	 * @return 200(OK) / MemberMyPageResponse
	 */
	@GetMapping
	public ResponseEntity<MemberMyPageResponse> getMember(@CookieValue(name = "Access-Token", required = false) String accessToken) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
		return ResponseEntity.ok(memberService.getMember(memberId));
	}

	/**
	 * 회원 정보 수정 요청
	 * @param memberId Long
	 * @param request MemberUpdateRequest
	 * @return 200(OK) / MemberUpdateResponse
	 */
	@PutMapping
	public ResponseEntity<MemberUpdateResponse> updateMember(
			@Valid @RequestBody MemberUpdateRequest request,
			@CookieValue(name = "Access-Token", required = false) String accessToken
		) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
		return ResponseEntity.ok(memberService.updateMember(memberId, request));
	}

	@PutMapping("/password")
	public ResponseEntity<MemberPasswordUpdateResponse> updateMemberPassword(
			@Valid @RequestBody  MemberPasswordUpdateRequest request,
			@CookieValue(name = "Access-Token", required = false) String accessToken
	) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
		return ResponseEntity.ok(memberService.updateMemberPassword(memberId, request));
	}

	@PutMapping("/reactivate")
	public ResponseEntity<Void> reactivateMember(
			@CookieValue(name = "Access-Token", required = false) String accessToken,
			@Valid @RequestBody  MemberPasswordUpdateRequest request
	) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
		memberService.reactivateMember(memberId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 회원 삭제에 관한 요청을 처리한다.
	 * @param memberId Long
	 * @return 204(NO CONTENT)
	 */
	@DeleteMapping
	public ResponseEntity<Void> deleteMember(
		@CookieValue(name = "Access-Token", required = false) String accessToken
	) {
		Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
		memberService.deleteMember(memberId);
		return ResponseEntity.noContent().build();
	}

}

