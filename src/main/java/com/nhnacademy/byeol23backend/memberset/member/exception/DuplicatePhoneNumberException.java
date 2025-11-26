package com.nhnacademy.byeol23backend.memberset.member.exception;

public class DuplicatePhoneNumberException extends RuntimeException {
	public DuplicatePhoneNumberException(String message) {
		super(message);
	}
}
