package com.nhnacademy.byeol23backend.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비회원 주문 조회 요청")
public record NonmemberOrderRequest(
	@Schema(description = "주문 번호", example = "ORD-2024-001")
	String orderNumber,

	@Schema(description = "주문 비밀번호", example = "1234")
	String orderPassword
) {
}
