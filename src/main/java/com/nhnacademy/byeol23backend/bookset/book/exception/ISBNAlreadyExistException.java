package com.nhnacademy.byeol23backend.bookset.book.exception;

public class ISBNAlreadyExistException extends RuntimeException {
	public ISBNAlreadyExistException(String message) {
		super(message);
	}
}
