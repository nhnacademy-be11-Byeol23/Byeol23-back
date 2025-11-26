package com.nhnacademy.byeol23backend.bookset.contributor.exception;

public class ContributorAlreadyExistsException extends RuntimeException {
	public ContributorAlreadyExistsException(String message) {
		super(message);
	}
}
