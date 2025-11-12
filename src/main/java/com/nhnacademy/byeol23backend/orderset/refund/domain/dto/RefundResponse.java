package com.nhnacademy.byeol23backend.orderset.refund.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundOption;

public record RefundResponse(String orderNumber,
							 String refundReason,
							 RefundOption refundOption,
							 BigDecimal refundPrice,
							 LocalDateTime refundedAt) {
}
