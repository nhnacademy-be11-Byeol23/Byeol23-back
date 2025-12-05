package com.nhnacademy.byeol23backend.memberset.member.dto;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;

//grade 값 넣어서
@Schema(description = "회원 등급 응답")
public record MemberGradeResponse(
	@Schema(description = "회원 정보")
	Member member
) {
}
