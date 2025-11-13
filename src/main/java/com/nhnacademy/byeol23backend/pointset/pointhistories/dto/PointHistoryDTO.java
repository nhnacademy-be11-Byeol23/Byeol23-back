package com.nhnacademy.byeol23backend.pointset.pointhistories.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointHistoryDTO(
	BigDecimal pointAmount,
	LocalDateTime createdAt,
	String pointPolicyName
) {
}
