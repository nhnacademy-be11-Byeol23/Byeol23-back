package com.nhnacademy.byeol23backend.orderset.payment.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentConfirmResponse(String paymentKey,
									 String orderId,
									 String orderName,
									 String status,
									 BigDecimal totalAmount,
									 LocalDateTime paymentRequestAt,
									 LocalDateTime paymentApprovedAt,
									 String method) {

}
