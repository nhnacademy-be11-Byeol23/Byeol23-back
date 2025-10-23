package com.nhnacademy.byeol23backend.orderset.payment.domain.dto;

import java.math.BigDecimal;

public record PaymentParamRequest(String orderId,
								  String paymentKey,
								  BigDecimal amount) {
}
