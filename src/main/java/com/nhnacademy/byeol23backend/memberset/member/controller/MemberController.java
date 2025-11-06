package com.nhnacademy.byeol23backend.memberset.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;
import com.nhnacademy.byeol23backend.memberset.member.dto.MemberResponseDto;
import com.nhnacademy.byeol23backend.memberset.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/register")
	public ResponseEntity<MemberResponseDto> createMember(@RequestBody Member member) {
		MemberResponseDto createdMember = memberService.createMember(member);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(createdMember);
	}

	@GetMapping("/{member-id}")
	public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long memberId) {
		return ResponseEntity.ok(memberService.getMember(memberId));
	}

	/**
	 * 회원 삭제에 관한 요청을 처리한다.
	 * @param memberId 회원아이디
	 * @return ResponseEntity&lt;void&gt; NoContent&lt;204&gt; HTTP 응답 반환
	 */
	@DeleteMapping("/{member-id}")
	public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
		memberService.deleteMember(memberId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{member-id}")
	public ResponseEntity<MemberResponseDto> updateMember(@PathVariable Long memberId, @RequestBody Member member) {
		memberService.updateMember(member);
		MemberResponseDto updatedMember = memberService.getMember(memberId);
		return ResponseEntity.ok(updatedMember);
	}

}

