package com.nhnacademy.byeol23backend.orderset.payment.domain.dto;

public record PaymentCancelResponse(String paymentKey,
									String cancelReason) {

}
