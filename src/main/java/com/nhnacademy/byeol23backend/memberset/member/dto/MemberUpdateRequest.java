package com.nhnacademy.byeol23backend.memberset.member.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 수정 요청")
public record MemberUpdateRequest(
	@Schema(description = "회원 이름", example = "홍길동")
	String memberName,

	@Schema(description = "닉네임", example = "길동이")
	String nickname,

	@Schema(description = "전화번호", example = "01012345678")
	String phoneNumber,

	@Schema(description = "이메일", example = "wsfa1223@gmail.com")
	String email,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "생년월일", example = "1990-12-01")
	LocalDate birthDate
) {
}
