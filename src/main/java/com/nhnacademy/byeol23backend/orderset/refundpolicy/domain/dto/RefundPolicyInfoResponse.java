package com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto;

import java.time.LocalDateTime;

public record RefundPolicyInfoResponse(String refundPolicyName,
									   String refundCondition,
									   String comment,
									   LocalDateTime changedAt) {
}
