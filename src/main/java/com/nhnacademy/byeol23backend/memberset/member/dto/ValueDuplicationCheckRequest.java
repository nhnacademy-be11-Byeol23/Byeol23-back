package com.nhnacademy.byeol23backend.memberset.member.dto;

public record ValueDuplicationCheckRequest(
	String loginId,
	String nickname,
	String phoneNumber,
	String email
){}
