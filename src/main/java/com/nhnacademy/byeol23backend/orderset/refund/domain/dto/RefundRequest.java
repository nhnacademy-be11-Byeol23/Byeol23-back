package com.nhnacademy.byeol23backend.orderset.refund.domain.dto;

import java.math.BigDecimal;

import com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.RefundOption;

public record RefundRequest(String orderNumber,
							String refundReason,
							RefundOption refundOption,
							BigDecimal appliedFee) {
}
