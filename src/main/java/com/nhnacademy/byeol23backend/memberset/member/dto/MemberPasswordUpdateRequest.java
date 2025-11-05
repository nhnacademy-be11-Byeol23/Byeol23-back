package com.nhnacademy.byeol23backend.memberset.member.dto;

public record MemberPasswordUpdateRequest(
	String loginId,
	String currentPassword,
	String newPassword
) {
}
