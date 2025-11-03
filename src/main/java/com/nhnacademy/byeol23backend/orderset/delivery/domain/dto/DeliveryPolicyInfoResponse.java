package com.nhnacademy.byeol23backend.orderset.delivery.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryPolicyInfoResponse(BigDecimal freeDeliveryCondition,
										 BigDecimal deliveryFee,
										 LocalDateTime changedAt) {

	public static DeliveryPolicyInfoResponse of(BigDecimal freeDeliveryCondition, BigDecimal deliveryFee) {
		return new DeliveryPolicyInfoResponse(freeDeliveryCondition, deliveryFee, null);
	}
}
