package com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto;

import java.time.LocalDateTime;

public record RefundPolicyCreateResponse(Long refundPolicyId,
										 String refundPolicyName,
										 String refundCondition,
										 String comment,
										 LocalDateTime changedAt) {
}
