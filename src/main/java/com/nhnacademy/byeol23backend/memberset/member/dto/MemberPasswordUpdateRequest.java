package com.nhnacademy.byeol23backend.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비밀번호 변경 요청")
public record MemberPasswordUpdateRequest(
	@Schema(description = "로그인 ID", example = "user123")
	String loginId,

	@Schema(description = "현재 비밀번호", example = "OldP@ssw0rd!")
	String currentPassword,

	@Schema(description = "새 비밀번호", example = "NewP@ssw0rd!")
	String newPassword
) {
}
