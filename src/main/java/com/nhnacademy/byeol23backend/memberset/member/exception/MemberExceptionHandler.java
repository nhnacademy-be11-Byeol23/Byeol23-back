package com.nhnacademy.byeol23backend.memberset.member.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.byeol23backend.commons.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

/* ControllerAdvice는 ResponseBody가 자동으로 붙지 않는다.
 * 그렇기 때문에 ResponseEntity가 아닌 객체를 리턴할 때 JSON Serialize가 안될 수 있기 때문에
 */
@RestControllerAdvice
public class MemberExceptionHandler {
	
	@ExceptionHandler(MemberNotFoundException.class)
	@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse(
				HttpStatus.NOT_FOUND.value(),
				e.getMessage(),
				request.getRequestURI(),
				LocalDateTime.now()
			));
	}

	@ExceptionHandler(DuplicatePhoneNumberException.class)
	@ApiResponse(responseCode = "409", description = "중복된 전화번호",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ErrorResponse> duplicatePhoneNumber(DuplicatePhoneNumberException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(409, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
	}

	@ExceptionHandler(DuplicateEmailException.class)
	@ApiResponse(responseCode = "409", description = "중복된 이메일",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ErrorResponse> duplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(409, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
	}
	@ExceptionHandler(DuplicateNicknameException.class)
	@ApiResponse(responseCode = "409", description = "중복된 닉네임",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ErrorResponse> duplicateNickname(DuplicateNicknameException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse(409, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
	}

	@ExceptionHandler(IncorrectPasswordException.class)
	@ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ErrorResponse> handleIncorrectPasswordException(IncorrectPasswordException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				e.getMessage(),
				request.getRequestURI(),
				LocalDateTime.now()
			));
	}
}
