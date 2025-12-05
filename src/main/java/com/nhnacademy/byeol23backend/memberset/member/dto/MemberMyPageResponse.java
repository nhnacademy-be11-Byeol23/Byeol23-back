package com.nhnacademy.byeol23backend.memberset.member.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.byeol23backend.memberset.address.dto.AddressResponse;
import com.nhnacademy.byeol23backend.memberset.grade.dto.AllGradeResponse;
import com.nhnacademy.byeol23backend.memberset.member.domain.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이 페이지 응답")
public record MemberMyPageResponse(
	@Schema(description = "로그인 ID", example = "user123")
	String loginId,

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
	LocalDate birthDate,

	@Schema(description = "현재 포인트", example = "10000")
	BigDecimal currentPoint,

	@Schema(description = "회원 역할", example = "MEMBER")
	Role memberRole,

	@Schema(description = "등급 이름", example = "일반")
	String gradeName,

	@Schema(description = "주소 정보")
	AddressResponse address,

	@Schema(description = "등급 목록")
	List<AllGradeResponse> grades
) {
}
