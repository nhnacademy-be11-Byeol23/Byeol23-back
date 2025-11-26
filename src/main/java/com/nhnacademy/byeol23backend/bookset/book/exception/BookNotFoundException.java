package com.nhnacademy.byeol23backend.bookset.book.exception;


public class BookNotFoundException extends RuntimeException {
	public BookNotFoundException(String message) {
		super(message);
	}
}
