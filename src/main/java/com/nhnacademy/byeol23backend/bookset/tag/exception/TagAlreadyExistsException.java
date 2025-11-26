package com.nhnacademy.byeol23backend.bookset.tag.exception;

public class TagAlreadyExistsException extends RuntimeException {
	public TagAlreadyExistsException(String message) {
		super(message);
	}
}
