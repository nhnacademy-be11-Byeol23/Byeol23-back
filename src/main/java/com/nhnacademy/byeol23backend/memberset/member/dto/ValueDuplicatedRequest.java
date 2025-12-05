package com.nhnacademy.byeol23backend.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "값 중복 확인 요청")
public record ValueDuplicatedRequest(
	@Schema(description = "닉네임", example = "길동이")
	String nickname,

	@Schema(description = "전화번호", example = "01012345678")
	String phoneNumber,

	@Schema(description = "이메일", example = "wsfa1223@gmail.com")
	String email
) {
}
