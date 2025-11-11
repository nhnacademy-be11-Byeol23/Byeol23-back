package com.nhnacademy.byeol23backend.memberset.member.dto;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

//grade 값 넣어서
public record MemberGradeResponse(
	Member member
) {
}
