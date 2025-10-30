package com.nhnacademy.byeol23backend.orderset.payment.exception;

public class PaymentNotFoundException extends RuntimeException {
	public PaymentNotFoundException(String message) {
		super(message);
	}
}
