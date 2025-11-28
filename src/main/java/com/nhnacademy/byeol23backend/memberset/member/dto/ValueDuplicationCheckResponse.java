package com.nhnacademy.byeol23backend.memberset.member.dto;

public record ValueDuplicationCheckResponse(
	boolean isDuplicatedId,
	boolean isDuplicatedNickname,
	boolean isDuplicatedEmail,
	boolean isDuplicatedPhoneNumber
) {}
