package com.nhnacademy.byeol23backend.orderset.delivery.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryPolicyCreateResponse(Long deliveryPolicyId,
										   BigDecimal freeDeliveryCondition,
										   BigDecimal deliveryFee,
										   LocalDateTime changedAt) {
}
