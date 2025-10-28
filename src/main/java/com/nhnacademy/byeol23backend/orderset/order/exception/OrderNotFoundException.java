package com.nhnacademy.byeol23backend.orderset.order.exception;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(String message) {
		super(message);
	}
}
