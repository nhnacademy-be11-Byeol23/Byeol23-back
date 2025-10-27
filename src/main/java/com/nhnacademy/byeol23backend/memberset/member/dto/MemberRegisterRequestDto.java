package com.nhnacademy.byeol23backend.memberset.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequestDto(
	@NotBlank(message = "로그인 ID는 필수 입력 값입니다.")
	@Size(min = 5, max = 20, message = "로그인 ID는 5자 이상 20자 이하로 입력해야 합니다.")
	@Schema(description = "로그인 ID", example = "user123")
	String loginId,

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 255, message = "비밀번호는 8자 이상 입력해야 합니다.")
	@Schema(description = "비밀번호", example = "P@ssw0rd!")
	String password,

	@NotBlank(message = "회원 이름은 필수 입력 값입니다.")
	@Size(max = 10, message = "회원 이름은 최대 10자까지 입력 가능합니다.")
	@Schema(description = "회원 이름", example = "홍길동")
	String memberName,

	@NotBlank(message = "닉네임은 필수 입력 값입니다.")
	@Size(max = 15, message = "닉네임은 최대 15자까지 입력 가능합니다.")
	@Schema(description = "닉네임", example = "길동이")
	String nickname,

	@NotBlank(message = "전화번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자로 입력해야 합니다.")
	@Schema(description = "전화번호", example = "01012345678")
	String phoneNumber,

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "유효한 이메일 형식이 아닙니다.")
	@Size(max = 30, message = "이메일은 최대 30자까지 입력 가능합니다.")
	@Schema(description = "이메일", example = "wsfa1223@gmail.com")
	String email,

	@NotBlank(message = "생년월일은 필수 입력 값입니다.")
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 YYYY-MM-DD 형식으로 입력해야 합니다.")
	@Schema(description = "생년월일", example = "1990-12-01")
	String birthday
) {
}
