package com.nhnacademy.byeol23backend.orderset.order.exception;

public class OrderPasswordNotMatchException extends RuntimeException {
	public OrderPasswordNotMatchException(String message) {
		super(message);
	}
}
