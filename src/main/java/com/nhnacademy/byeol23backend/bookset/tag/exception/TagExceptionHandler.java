package com.nhnacademy.byeol23backend.bookset.tag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TagExceptionHandler {

	@ExceptionHandler(TagAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleTagNotFoundException(TagNotFoundException e) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(TagAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleTagAlreadyExistsException(TagAlreadyExistsException e) {
		ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
}
