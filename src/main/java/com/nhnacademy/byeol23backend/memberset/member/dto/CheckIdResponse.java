package com.nhnacademy.byeol23backend.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ID 중복 확인 응답")
public record CheckIdResponse(
	@Schema(description = "중복 여부", example = "false")
	boolean isDuplicated
) {
}
