package com.nhnacademy.byeol23backend.memberset.member.exception;

public
class DuplicateNicknameException extends RuntimeException {
	public DuplicateNicknameException(String message) {
		super(message);
	}
}