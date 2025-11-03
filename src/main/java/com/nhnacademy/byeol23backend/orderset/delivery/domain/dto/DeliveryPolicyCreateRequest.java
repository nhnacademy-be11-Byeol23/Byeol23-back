package com.nhnacademy.byeol23backend.orderset.delivery.domain.dto;

import java.math.BigDecimal;

public record DeliveryPolicyCreateRequest(BigDecimal freeDeliveryCondition,
										  BigDecimal deliveryFee) {
}
