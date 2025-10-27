package com.nhnacademy.byeol23backend.orderset.payment.domain.dto;

public record PaymentCancelRequest(String cancelReason,
								   String paymentKey) {
}
