package com.nhnacademy.byeol23backend.commons.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(PermissionDeniedException.class)
	public ErrorResponse handlePermissionDeniedException(PermissionDeniedException e, HttpServletRequest request) {
		return new ErrorResponse(
			HttpStatus.UNAUTHORIZED.value(),
			e.getMessage(),
			request.getRequestURI(),
			LocalDateTime.now()
		);
	}
}
