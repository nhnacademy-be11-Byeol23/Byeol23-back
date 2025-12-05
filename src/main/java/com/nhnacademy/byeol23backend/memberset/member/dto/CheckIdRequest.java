package com.nhnacademy.byeol23backend.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ID 중복 확인 요청")
public record CheckIdRequest(
	@Schema(description = "확인할 로그인 ID", example = "user123")
	String loginId
) {
}
