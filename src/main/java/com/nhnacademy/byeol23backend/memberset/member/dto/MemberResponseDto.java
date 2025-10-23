package com.nhnacademy.byeol23backend.memberset.member.dto;

import com.nhnacademy.byeol23backend.memberset.member.domain.Member;

// todo. 추후에 필요한 필드값만 넣어서 변경해야 함
public record MemberResponseDto(Member member) {
}
