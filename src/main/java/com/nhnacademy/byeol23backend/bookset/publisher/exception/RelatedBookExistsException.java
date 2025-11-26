package com.nhnacademy.byeol23backend.bookset.publisher.exception;

public class RelatedBookExistsException extends RuntimeException {
	public RelatedBookExistsException(String message) {
		super(message);
	}
}
