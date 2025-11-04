package com.nhnacademy.byeol23backend.orderset.refundpolicy.domain.dto;

public record RefundPolicyCreateRequest(String refundPolicyName,
										String refundCondition,
										String comment) {

}
