package com.nhnacademy.byeol23backend.memberset.grade.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record GradeDto(
	@NotNull
	String gradeName,

	@NotNull
	BigDecimal criterionPrice
) {
}
