package com.nhnacademy.byeol23backend.memberset.member.dto;

import java.time.LocalDate;

public record MemberUpdateRequest(
	String memberName,
	String phoneNumber,
	String email,
	LocalDate birthDate
) {
}
