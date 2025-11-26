package com.nhnacademy.byeol23backend.memberset.member.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.byeol23backend.commons.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/* ControllerAdvice는 ResponseBody가 자동으로 붙지 않는다.
 * 그렇기 때문에 ResponseEntity가 아닌 객체를 리턴할 때 JSON Serialize가 안될 수 있기 때문에
 */
@RestControllerAdvice
public class MemberExceptionHandler {
	
	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(DuplicatePhoneNumberException.class)
	public ResponseEntity<ErrorResponse> duplicatePhoneNumber(DuplicatePhoneNumberException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(409, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> duplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(409, e.getMessage(), request.getRequestURI(), LocalDateTime.now()));
	}
}
