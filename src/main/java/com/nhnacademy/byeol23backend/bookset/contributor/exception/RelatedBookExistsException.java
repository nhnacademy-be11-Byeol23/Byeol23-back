package com.nhnacademy.byeol23backend.bookset.contributor.exception;

public class RelatedBookExistsException extends RuntimeException {
	public RelatedBookExistsException(String message) {
		super(message);
	}
}
